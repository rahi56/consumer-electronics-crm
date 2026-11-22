
package com.rahim.crm.technician.service;

import com.rahim.crm.Security.CustomUserPrincipal;
import com.rahim.crm.branch.entity.Branch;
import com.rahim.crm.branch.repository.BranchRepository;
import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;
import com.rahim.crm.auth.UserRepository;
import com.rahim.crm.technician.dto.TechnicianCreateResponse;
import com.rahim.crm.technician.dto.TechnicianRequest;
import com.rahim.crm.technician.dto.TechnicianResponse;
import com.rahim.crm.technician.dto.TechnicianUpdateRequest;
import com.rahim.crm.technician.repository.TechnicianRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public TechnicianService(
            TechnicianRepository technicianRepository,
            BranchRepository branchRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.technicianRepository = technicianRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // CREATE TECHNICIAN
    // ADMIN: Any active branch
    // BRANCH_MANAGER: Own branch only
    // =====================================================

    @Transactional
    public TechnicianCreateResponse createTechnician(
            TechnicianRequest request) {

        if (technicianRepository.existsByUsernameIgnoreCase(
                request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already exists.");
        }

        if (technicianRepository.existsByEmailIgnoreCase(
                request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists.");
        }

        Branch branch = branchRepository.findById(
                request.getBranchId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Branch not found.")
        );

        ensureCanAccessBranch(branch);

        if (!branch.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign technician to an inactive branch.");
        }

        String temporaryPassword = generateTemporaryPassword();

        User technician = new User();

        technician.setName(request.getName());
        technician.setUsername(request.getUsername());
        technician.setEmail(request.getEmail());
        technician.setPassword(
                passwordEncoder.encode(temporaryPassword));

        technician.setPhone(request.getPhone());
        technician.setAddress(request.getAddress());
        technician.setCity(request.getCity());
        technician.setState(request.getState());
        technician.setZip(request.getZip());

        technician.setRole(Role.TECHNICIAN);
        technician.setBranch(branch);
        technician.setEnabled(true);
        technician.setMustChangePassword(true);

        technician = technicianRepository.saveAndFlush(technician);

        String technicianId =
                String.format("TECH-%06d", technician.getId());

        technician.setTechnicianId(technicianId);

        technician = technicianRepository.save(technician);

        return new TechnicianCreateResponse(
                technician.getId(),
                technician.getTechnicianId(),
                technician.getName(),
                technician.getUsername(),
                technician.getEmail(),
                temporaryPassword,
                branch.getId(),
                branch.getName()
        );
    }

    // =====================================================
    // GET TECHNICIANS BY BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<TechnicianResponse> getTechniciansByBranch(
            Long branchId) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Branch not found.")
                );

        ensureCanAccessBranch(branch);

        List<User> technicians =
                technicianRepository.findAllByBranch_IdAndRole(
                        branchId,
                        Role.TECHNICIAN
                );

        return technicians.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET TECHNICIAN BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public TechnicianResponse getTechnicianById(
            Long technicianId) {

        User technician = findTechnician(technicianId);

        ensureCanManageTechnician(technician);

        return mapToResponse(technician);
    }

    // =====================================================
    // UPDATE TECHNICIAN
    // =====================================================

    @Transactional
    public TechnicianResponse updateTechnician(
            Long technicianId,
            TechnicianUpdateRequest request) {

        User technician = findTechnician(technicianId);

        ensureCanManageTechnician(technician);

        technician.setName(request.getName());
        technician.setPhone(request.getPhone());
        technician.setAddress(request.getAddress());
        technician.setCity(request.getCity());
        technician.setState(request.getState());
        technician.setZip(request.getZip());

        technician = technicianRepository.save(technician);

        return mapToResponse(technician);
    }

    // =====================================================
    // DEACTIVATE TECHNICIAN
    // =====================================================

    @Transactional
    public TechnicianResponse deactivateTechnician(
            Long technicianId) {

        User technician = findTechnician(technicianId);

        ensureCanManageTechnician(technician);

        technician.setEnabled(false);

        technician = technicianRepository.save(technician);

        return mapToResponse(technician);
    }

    // =====================================================
    // ACTIVATE TECHNICIAN
    // =====================================================

    @Transactional
    public TechnicianResponse activateTechnician(
            Long technicianId) {

        User technician = findTechnician(technicianId);

        ensureCanManageTechnician(technician);

        if (technician.getBranch() == null ||
                !technician.getBranch().isActive()) {

            throw new IllegalArgumentException(
                    "Cannot activate a technician assigned to an inactive branch.");
        }

        technician.setEnabled(true);

        technician = technicianRepository.save(technician);

        return mapToResponse(technician);
    }

    // =====================================================
    // REASSIGN TECHNICIAN
    // ADMIN ONLY
    // =====================================================

    @Transactional
    public TechnicianResponse reassignTechnician(
            Long technicianId,
            Long newBranchId) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Only ADMIN can reassign technicians.");
        }

        User technician = findTechnician(technicianId);

        Branch newBranch = branchRepository.findById(newBranchId)
                .orElseThrow(() ->
                        new IllegalArgumentException("New branch not found.")
                );

        if (!newBranch.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign technician to an inactive branch.");
        }

        technician.setBranch(newBranch);

        technician = technicianRepository.save(technician);

        return mapToResponse(technician);
    }

    // =====================================================
    // AUTHORIZATION HELPERS
    // =====================================================

    // Retrieve the currently authenticated user from database.

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof CustomUserPrincipal principal)) {

            throw new AccessDeniedException(
                    "Authenticated user not found.");
        }

        return userRepository.findById(principal.getId())
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "User account no longer exists.")
                );
    }

    // ADMIN can access every branch.
    // BRANCH_MANAGER can access only their own branch.

    private void ensureCanAccessBranch(Branch targetBranch) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.BRANCH_MANAGER) {

            Branch managerBranch = currentUser.getBranch();

            if (managerBranch != null &&
                    targetBranch != null &&
                    Objects.equals(
                            managerBranch.getId(),
                            targetBranch.getId())) {

                return;
            }
        }

        throw new AccessDeniedException(
                "You do not have permission to access this branch.");
    }

    // Verify technician role and branch ownership.

    private void ensureCanManageTechnician(User technician) {

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException(
                    "The requested user is not a technician.");
        }

        ensureCanAccessBranch(technician.getBranch());
    }

    // Retrieve a user and ensure the requested user is a technician.

    private User findTechnician(Long technicianId) {

        User technician = technicianRepository.findById(technicianId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Technician not found.")
                );

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException(
                    "The requested user is not a technician.");
        }

        return technician;
    }

    // =====================================================
    // RESPONSE MAPPER
    // =====================================================

    private TechnicianResponse mapToResponse(User technician) {

        Branch branch = technician.getBranch();

        return new TechnicianResponse(
                technician.getId(),
                technician.getTechnicianId(),
                technician.getName(),
                technician.getUsername(),
                technician.getEmail(),
                technician.getPhone(),
                branch != null ? branch.getId() : null,
                branch != null ? branch.getName() : null,
                technician.isEnabled()
        );
    }

    // =====================================================
    // TEMPORARY PASSWORD GENERATOR
    // =====================================================

    private String generateTemporaryPassword() {

        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "abcdefghijklmnopqrstuvwxyz" +
                        "0123456789" +
                        "!@#$%&*";

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 16; i++) {

            int index = secureRandom.nextInt(characters.length());

            password.append(characters.charAt(index));
        }

        return password.toString();
    }
}
