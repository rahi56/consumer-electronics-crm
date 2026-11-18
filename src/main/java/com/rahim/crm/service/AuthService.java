
package com.rahim.crm.service;
import com.rahim.crm.dto.ForgotPasswordResponse;
import com.rahim.crm.dto.RegisterRequest;
import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;
import com.rahim.crm.entity.PasswordResetOtp;
import com.rahim.crm.repository.UserRepository;
import com.rahim.crm.repository.PasswordResetOtpRepository;
import com.rahim.crm.Security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetOtpRepository otpRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordResetOtpRepository otpRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpRepository = otpRepository;
    }

    // =========================
    // REGISTER
    // =========================

    @Transactional
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = new User();

        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZip(request.getZip());

        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        userRepository.save(user);

        return "Registration successful";
    }

    // =========================
    // LOGIN
    // =========================

    public String login(String email, String password) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                password
                        )
                );

        return jwtService.generateToken(authentication.getName());
    }

    // =========================
    // CHANGE PASSWORD
    // =========================

    @Transactional
    public String changePassword(
            String email,
            String oldPassword,
            String newPassword) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException(
                    "New password must be different from old password"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return "Password changed successfully";
    }

    // =========================
    // FORGOT PASSWORD
    // =========================

    @Transactional
    public ForgotPasswordResponse forgetPassword(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElse(null);

        if (user == null) {
            return new ForgotPasswordResponse(
                    "If the account exists, an OTP has been generated.",
                    null
            );
        }

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        otpRepository.deleteByEmailIgnoreCase(user.getEmail());

        PasswordResetOtp resetOtp = new PasswordResetOtp();

        resetOtp.setEmail(user.getEmail().toLowerCase());
        resetOtp.setOtp(otp);
        resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        resetOtp.setVerified(false);

        otpRepository.save(resetOtp);

        return new ForgotPasswordResponse(
                "OTP generated successfully. Valid for 5 minutes.",
                otp
        );
    }


    // =========================
    // VERIFY OTP
    // =========================

    @Transactional
    public String verifyOtp(String email, String otp) {

        PasswordResetOtp resetOtp =
                otpRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid or expired OTP")
                        );

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(resetOtp);
            throw new RuntimeException("OTP has expired");
        }

        if (!resetOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Incorrect OTP");
        }

        resetOtp.setVerified(true);
        otpRepository.save(resetOtp);

        return "OTP verified successfully";
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Transactional
    public String resetPassword(String email, String newPassword) {

        PasswordResetOtp resetOtp =
                otpRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() ->
                                new RuntimeException("Please verify OTP first")
                        );

        if (!resetOtp.isVerified()) {
            throw new RuntimeException("Please verify OTP first");
        }

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(resetOtp);
            throw new RuntimeException("OTP has expired");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException(
                    "New password must be different from old password"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // OTP must be single-use.
        otpRepository.delete(resetOtp);

        return "Password reset successfully";
    }
}
