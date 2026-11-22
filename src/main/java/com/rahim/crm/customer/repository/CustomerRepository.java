
package com.rahim.crm.customer.repository;

import com.rahim.crm.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmailIgnoreCase(String email);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByPhone(String phone);

    boolean existsByEmailIgnoreCase(String email);

    // Check phone belongs to another customer
    boolean existsByPhoneAndIdNot(String phone, Long id);

    // Check email belongs to another customer
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    List<Customer> findByCustomerCodeContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(
            String customerCode,
            String phone,
            String email
    );
}
