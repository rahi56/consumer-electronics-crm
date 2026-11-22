
package com.rahim.crm.auth;

import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByRole(Role role);
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findAllByBranch_IdAndRole(Long branchId, Role role);
    Optional<User> findByUsernameIgnoreCase(String username);
}
