
package com.rahim.crm.complaint.repository;

import com.rahim.crm.complaint.entity.Complaint;
import com.rahim.crm.complaint.entity.ComplaintStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    boolean existsByComplaintNumber(String complaintNumber);

    List<Complaint> findByCustomer_User_IdOrderByCreatedAtDesc(Long userId);

    Optional<Complaint> findByIdAndCustomer_User_Id(
            Long complaintId,
            Long userId
    );

    List<Complaint> findByStatus(ComplaintStatus status);

    // Find complaints assigned to a particular branch manager
    List<Complaint> findByAssignedManager_IdOrderByCreatedAtDesc(
            Long managerId
    );
}
