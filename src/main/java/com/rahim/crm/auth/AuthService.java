
package com.rahim.crm.auth;

import com.rahim.crm.dto.AuthResponse;
import com.rahim.crm.dto.ForgotPasswordResponse;
import com.rahim.crm.dto.RegisterRequest;
import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;
import com.rahim.crm.entity.PasswordResetOtp;
import com.rahim.crm.Security.JwtService;
import com.rahim.crm.customer.entity.Customer;
import com.rahim.crm.customer.repository.CustomerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetOtpRepository otpRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordResetOtpRepository otpRepository) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpRepository = otpRepository;
    }

    // =========================
    // REGISTER CUSTOMER
    // =========================

    @Transactional
    public String register(RegisterRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String phone = request.getPhone().trim();

        String username = request.getUsername().trim();

        // Check existing login account
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }

        // Do not allow public registration to claim an
        // existing staff-created customer profile.
        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A customer profile already exists with this email. Please contact support."
            );
        }

        if (customerRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A customer profile already exists with this phone number."
            );
        }

        // Create login account
        User user = new User();

        user.setName(request.getName().trim());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(phone);
        user.setAddress(request.getAddress().trim());
        user.setCity(request.getCity().trim());
        user.setState(request.getState().trim());
        user.setZip(request.getZip().trim());

        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);
        user.setMustChangePassword(false);

        User savedUser = userRepository.save(user);

        // Create customer profile
        Customer customer = new Customer();

        customer.setCustomerCode(generateCustomerCode());
        customer.setFullName(savedUser.getName());
        customer.setEmail(savedUser.getEmail());
        customer.setPhone(savedUser.getPhone());
        customer.setAddress(savedUser.getAddress());
        customer.setActive(true);

        // Link customer profile to login account
        customer.setUser(savedUser);

        customerRepository.save(customer);

        return "Registration successful";
    }

    // =========================
    // GENERATE CUSTOMER CODE
    // =========================

    private String generateCustomerCode() {

        String customerCode;

        do {
            customerCode = "CUS-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase(Locale.ROOT);

        } while (customerRepository.existsByCustomerCode(customerCode));

        return customerCode;
    }

    // =========================
    // LOGIN
    // =========================

    public AuthResponse login(String email, String password) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                password
                        )
                );

        String authenticatedEmail = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(authenticatedEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        String token = jwtService.generateToken(authenticatedEmail);

        return new AuthResponse(
                token,
                "Bearer",
                user.isMustChangePassword()
        );
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
        user.setMustChangePassword(false);

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

        resetOtp.setEmail(user.getEmail().toLowerCase(Locale.ROOT));
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
        user.setMustChangePassword(false);

        userRepository.save(user);

        // OTP is single-use
        otpRepository.delete(resetOtp);

        return "Password reset successfully";
    }
}
