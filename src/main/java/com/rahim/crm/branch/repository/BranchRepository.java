
package com.rahim.crm.branch.repository;

import com.rahim.crm.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    boolean existsByCode(String code);

}

