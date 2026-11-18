package com.rahim.crm.repository;

import com.rahim.crm.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmailIgnoreCase(String email);

    void deleteByEmailIgnoreCase(String email);
}