package com.rahim.crm.technician.repository;

import com.rahim.crm.entity.User;
import com.rahim.crm.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicianRepository extends JpaRepository<User, Long> {

    List<User> findAllByBranch_IdAndRole(Long branchId, Role role);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

}