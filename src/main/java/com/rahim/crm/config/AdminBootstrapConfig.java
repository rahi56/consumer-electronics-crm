
package com.rahim.crm.config;

import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;
import com.rahim.crm.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Value("${app.bootstrap-admin.enabled:false}")
    private boolean bootstrapEnabled;

    @Value("${app.bootstrap-admin.name:}")
    private String adminName;

    @Value("${app.bootstrap-admin.username:}")
    private String adminUsername;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!bootstrapEnabled) {
                return;
            }

            if (userRepository.existsByRole(Role.ADMIN)) {
                System.out.println("ADMIN account already exists.");
                return;
            }

            if (adminName.isBlank()
                    || adminUsername.isBlank()
                    || adminEmail.isBlank()
                    || adminPassword.isBlank()) {

                throw new IllegalStateException(
                        "Initial ADMIN credentials are missing.");
            }

            if (userRepository.existsByEmailIgnoreCase(adminEmail)
                    || userRepository.existsByUsernameIgnoreCase(adminUsername)) {

                throw new IllegalStateException(
                        "ADMIN email or username is already registered.");
            }

            if (adminPassword.length() < 12) {
                throw new IllegalStateException(
                        "Initial ADMIN password must contain at least 12 characters.");
            }

            User admin = new User();

            admin.setName(adminName);
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPhone("+8801700000000");
            admin.setAddress("Head Office");
            admin.setCity("Dhaka");
            admin.setState("Dhaka");
            admin.setZip("1000");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);

            userRepository.save(admin);

            System.out.println("Initial ADMIN account created successfully.");
        };
    }
}
