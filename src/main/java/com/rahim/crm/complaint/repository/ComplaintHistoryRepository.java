
package com.rahim.crm.complaint.repository;

import com.rahim.crm.complaint.entity.ComplaintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintHistoryRepository
        extends JpaRepository<ComplaintHistory, Long> {

    List<ComplaintHistory> findByComplaint_IdOrderByCreatedAtAsc(
            Long complaintId
    );
}
