
package com.rahim.crm.complaint.service;

import com.rahim.crm.auth.UserRepository;
import com.rahim.crm.branch.entity.Branch;
import com.rahim.crm.branch.repository.BranchRepository;

import com.rahim.crm.complaint.dto.ComplaintActionRequest;
import com.rahim.crm.complaint.dto.ComplaintHistoryResponse;
import com.rahim.crm.complaint.dto.ComplaintRequest;
import com.rahim.crm.complaint.dto.ComplaintResponse;

import com.rahim.crm.complaint.entity.Complaint;
import com.rahim.crm.complaint.entity.ComplaintActionType;
import com.rahim.crm.complaint.entity.ComplaintHistory;
import com.rahim.crm.complaint.entity.ComplaintStatus;

import com.rahim.crm.complaint.repository.ComplaintHistoryRepository;
import com.rahim.crm.complaint.repository.ComplaintRepository;

import com.rahim.crm.customer.entity.Customer;
import com.rahim.crm.customer.repository.CustomerRepository;

import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintHistoryRepository historyRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            ComplaintHistoryRepository historyRepository,
            CustomerRepository customerRepository,
            BranchRepository branchRepository,
            UserRepository userRepository
    ) {
        this.complaintRepository = complaintRepository;
        this.historyRepository = historyRepository;
        this.customerRepository = customerRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
    }

    // Create a new complaint and automatically assign branch and manager
    @Transactional
    public ComplaintResponse createComplaint(
            Long userId,
            ComplaintRequest request
    ) {
        Customer customer = getCustomer(userId);

        Complaint complaint = new Complaint();

        complaint.setComplaintNumber(generateComplaintNumber());
        complaint.setCustomer(customer);
        complaint.setSubject(request.getSubject().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setStatus(ComplaintStatus.OPEN);

        // Save customer's service location
        complaint.setServiceLatitude(request.getLatitude());
        complaint.setServiceLongitude(request.getLongitude());

        // Find nearest eligible branch and its manager
        assignNearestBranchAndManager(complaint);

        Complaint saved = complaintRepository.save(complaint);

        saveHistory(
                saved,
                customer.getUser(),
                ComplaintActionType.COMPLAINT_CREATED,
                "Complaint submitted by customer"
        );

        return mapToResponse(saved);
    }

    // Find nearest active branch with an enabled branch manager
    private void assignNearestBranchAndManager(Complaint complaint) {

        List<Branch> activeBranches = branchRepository.findByActiveTrue();

        Branch nearestBranch = null;
        User nearestManager = null;

        double shortestDistance = Double.MAX_VALUE;

        for (Branch branch : activeBranches) {

            // Find an enabled branch manager
            User manager = userRepository
                    .findFirstByBranch_IdAndRoleAndEnabledTrue(
                            branch.getId(),
                            Role.BRANCH_MANAGER
                    )
                    .orElse(null);

            // Skip branches without an enabled manager
            if (manager == null) {
                continue;
            }

            double distance = calculateDistance(
                    complaint.getServiceLatitude(),
                    complaint.getServiceLongitude(),
                    branch.getLatitude(),
                    branch.getLongitude()
            );

            if (distance < shortestDistance) {

                shortestDistance = distance;
                nearestBranch = branch;
                nearestManager = manager;
            }
        }

        // No eligible branch found
        if (nearestBranch == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No active branch with an enabled branch manager is available"
            );
        }

        // Assign branch and manager
        complaint.setBranch(nearestBranch);
        complaint.setAssignedManager(nearestManager);
    }

    // Calculate distance between two geographic coordinates using Haversine formula
    private double calculateDistance(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2
    ) {

        final double EARTH_RADIUS_KM = 6371.0;

        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);

        double latitudeDifference =
                Math.toRadians(latitude2 - latitude1);

        double longitudeDifference =
                Math.toRadians(longitude2 - longitude1);

        double a =
                Math.sin(latitudeDifference / 2)
                        * Math.sin(latitudeDifference / 2)
                        +
                        Math.cos(lat1)
                                * Math.cos(lat2)
                                * Math.sin(longitudeDifference / 2)
                                * Math.sin(longitudeDifference / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS_KM * c;
    }

    // View customer's complaints
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getMyComplaints(Long userId) {

        return complaintRepository
                .findByCustomer_User_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Reopen a resolved or closed complaint
    @Transactional
    public ComplaintResponse reopenComplaint(
            Long userId,
            Long complaintId,
            ComplaintActionRequest request
    ) {
        Customer customer = getCustomer(userId);

        Complaint complaint = getCustomerComplaint(
                complaintId,
                userId
        );

        if (complaint.getStatus() != ComplaintStatus.RESOLVED
                && complaint.getStatus() != ComplaintStatus.CLOSED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only resolved or closed complaints can be reopened"
            );
        }

        complaint.setStatus(ComplaintStatus.REOPENED);

        Complaint saved = complaintRepository.save(complaint);

        saveHistory(
                saved,
                customer.getUser(),
                ComplaintActionType.COMPLAINT_REOPENED,
                request.getReason().trim()
        );

        return mapToResponse(saved);
    }

    // Appeal a resolved or closed complaint
    @Transactional
    public ComplaintResponse appealComplaint(
            Long userId,
            Long complaintId,
            ComplaintActionRequest request
    ) {
        Customer customer = getCustomer(userId);

        Complaint complaint = getCustomerComplaint(
                complaintId,
                userId
        );

        if (complaint.getStatus() != ComplaintStatus.RESOLVED
                && complaint.getStatus() != ComplaintStatus.CLOSED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only resolved or closed complaints can be appealed"
            );
        }

        complaint.setStatus(ComplaintStatus.APPEALED);

        Complaint saved = complaintRepository.save(complaint);

        saveHistory(
                saved,
                customer.getUser(),
                ComplaintActionType.COMPLAINT_APPEALED,
                request.getReason().trim()
        );

        return mapToResponse(saved);
    }

    // View history of a customer's complaint
    @Transactional(readOnly = true)
    public List<ComplaintHistoryResponse> getComplaintHistory(
            Long userId,
            Long complaintId
    ) {
        getCustomerComplaint(complaintId, userId);

        return historyRepository
                .findByComplaint_IdOrderByCreatedAtAsc(complaintId)
                .stream()
                .map(this::mapToHistoryResponse)
                .toList();
    }

    // Find customer using authenticated user ID
    private Customer getCustomer(Long userId) {

        return customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer profile not found"
                ));
    }

    // Verify complaint ownership
    private Complaint getCustomerComplaint(
            Long complaintId,
            Long userId
    ) {
        return complaintRepository
                .findByIdAndCustomer_User_Id(complaintId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Complaint not found"
                ));
    }

    // Save an event in complaint history
    private void saveHistory(
            Complaint complaint,
            User performedBy,
            ComplaintActionType actionType,
            String reason
    ) {
        ComplaintHistory history = new ComplaintHistory();

        history.setComplaint(complaint);
        history.setPerformedBy(performedBy);
        history.setActionType(actionType);
        history.setReason(reason);

        historyRepository.save(history);
    }

    // Generate complaint reference number
    private String generateComplaintNumber() {
        return "CMP-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }


    // Map complaint entity to response DTO
    private ComplaintResponse mapToResponse(Complaint complaint) {

        return new ComplaintResponse(
                complaint.getId(),
                complaint.getComplaintNumber(),
                complaint.getSubject(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getResolutionNotes(),

                // Branch
                complaint.getBranch() != null
                        ? complaint.getBranch().getId() : null,
                complaint.getBranch() != null
                        ? complaint.getBranch().getName() : null,

                // Manager
                complaint.getAssignedManager() != null
                        ? complaint.getAssignedManager().getId() : null,
                complaint.getAssignedManager() != null
                        ? complaint.getAssignedManager().getName() : null,

                // Technician
                complaint.getAssignedTechnician() != null
                        ? complaint.getAssignedTechnician().getId() : null,
                complaint.getAssignedTechnician() != null
                        ? complaint.getAssignedTechnician().getName() : null,

                complaint.getResolvedAt(),
                complaint.getClosedAt(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt()
        );
    }

    // Map history entity to response DTO
    private ComplaintHistoryResponse mapToHistoryResponse(
            ComplaintHistory history
    ) {
        return new ComplaintHistoryResponse(
                history.getId(),
                history.getActionType(),
                history.getReason(),
                history.getManagerResponse(),
                history.getPerformedBy().getUsername(),
                history.getCreatedAt()
        );
    }

    // View complaints assigned to the authenticated branch manager
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getManagerComplaints(Long managerId) {

        return complaintRepository
                .findByAssignedManager_IdOrderByCreatedAtDesc(managerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Assign a technician to a complaint
    @Transactional
    public ComplaintResponse assignTechnician(
            Long managerId,
            Long complaintId,
            Long technicianId
    ) {

        // Find the complaint
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Complaint not found"
                ));

        // Verify that the complaint belongs to this manager
        if (complaint.getAssignedManager() == null
                || !complaint.getAssignedManager().getId().equals(managerId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to manage this complaint"
            );
        }

        // Find the selected technician
        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Technician not found"
                ));

        // Verify technician role
        if (technician.getRole() != Role.TECHNICIAN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected user is not a technician"
            );
        }

        // Verify technician is enabled
        if (!technician.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected technician is not active"
            );
        }

        // Verify technician belongs to the same branch
        if (technician.getBranch() == null
                || complaint.getBranch() == null
                || !technician.getBranch().getId()
                .equals(complaint.getBranch().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Technician must belong to the complaint's branch"
            );
        }

        // Assign technician
        complaint.setAssignedTechnician(technician);

        Complaint saved = complaintRepository.save(complaint);

// Record technician assignment in complaint history
        saveHistory(
                saved,
                complaint.getAssignedManager(),
                ComplaintActionType.TECHNICIAN_ASSIGNED,
                "Technician " + technician.getName()
                        + " assigned by branch manager"
        );

        return mapToResponse(saved);
    }


}
