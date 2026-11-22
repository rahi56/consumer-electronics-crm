package com.rahim.crm.branch.service;

import com.rahim.crm.branch.dto.BranchManagerRequest;
import com.rahim.crm.branch.dto.BranchManagerResponse;
import com.rahim.crm.branch.entity.Branch;
import com.rahim.crm.branch.repository.BranchRepository;
import com.rahim.crm.entity.Role;
import com.rahim.crm.entity.User;
import com.rahim.crm.auth.UserRepository;
import com.rahim.crm.branch.dto.BranchManagerUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BranchManagerService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public BranchManagerService(
            UserRepository userRepository,
            BranchRepository branchRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public BranchManagerResponse createManager(BranchManagerRequest request) {

        // 1. Check duplicate username
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username is already registered.");
        }

        // 2. Check duplicate email
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // 3. Find the requested branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Branch not found."));

        // 4. Do not assign managers to inactive branches
        if (!branch.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a manager to an inactive branch.");
        }

        // 5. Create user
        User manager = new User();

        manager.setName(request.getName());
        manager.setUsername(request.getUsername());
        manager.setEmail(request.getEmail());

        // Always store encoded password
        manager.setPassword(passwordEncoder.encode(request.getPassword()));

        manager.setPhone(request.getPhone());
        manager.setAddress(request.getAddress());
        manager.setCity(request.getCity());
        manager.setState(request.getState());
        manager.setZip(request.getZip());

        // Role is assigned by backend, never by client
        manager.setRole(Role.BRANCH_MANAGER);

        manager.setBranch(branch);
        manager.setEnabled(true);

        // 6. Save manager
        User savedManager = userRepository.save(manager);

        // 7. Return safe response
        return new BranchManagerResponse(
                savedManager.getId(),
                savedManager.getName(),
                savedManager.getUsername(),
                savedManager.getEmail(),
                savedManager.getPhone(),
                savedManager.getRole(),
                branch.getId(),
                branch.getName(),
                savedManager.isEnabled()
        );

    }

    @Transactional(readOnly = true)
    public List<BranchManagerResponse> getManagersByBranch(Long branchId) {

        // Verify that the branch exists
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Branch not found."));

        // Retrieve managers assigned to this branch
        List<User> managers =
                userRepository.findAllByBranch_IdAndRole(
                        branchId,
                        Role.BRANCH_MANAGER
                );

        // Convert entities into safe response DTOs
        return managers.stream()
                .map(manager -> new BranchManagerResponse(
                        manager.getId(),
                        manager.getName(),
                        manager.getUsername(),
                        manager.getEmail(),
                        manager.getPhone(),
                        manager.getRole(),
                        branch.getId(),
                        branch.getName(),
                        manager.isEnabled()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public BranchManagerResponse getManagerById(Long managerId) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found."));

        if (manager.getRole() != Role.BRANCH_MANAGER) {
            throw new IllegalArgumentException(
                    "The requested user is not a Branch Manager.");
        }

        Branch branch = manager.getBranch();

        return new BranchManagerResponse(
                manager.getId(),
                manager.getName(),
                manager.getUsername(),
                manager.getEmail(),
                manager.getPhone(),
                manager.getRole(),
                branch.getId(),
                branch.getName(),
                manager.isEnabled()
        );
    }


    @Transactional
    public BranchManagerResponse deactivateManager(Long managerId) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found."));

        if (manager.getRole() != Role.BRANCH_MANAGER) {
            throw new IllegalArgumentException(
                    "The requested user is not a Branch Manager.");
        }

        manager.setEnabled(false);

        User updatedManager = userRepository.save(manager);

        return mapToResponse(updatedManager);
    }

    @Transactional
    public BranchManagerResponse activateManager(Long managerId) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found."));

        if (manager.getRole() != Role.BRANCH_MANAGER) {
            throw new IllegalArgumentException(
                    "The requested user is not a Branch Manager.");
        }

        manager.setEnabled(true);

        User updatedManager = userRepository.save(manager);

        return mapToResponse(updatedManager);
    }

    private BranchManagerResponse mapToResponse(User manager) {

        Branch branch = manager.getBranch();

        return new BranchManagerResponse(
                manager.getId(),
                manager.getName(),
                manager.getUsername(),
                manager.getEmail(),
                manager.getPhone(),
                manager.getRole(),
                branch.getId(),
                branch.getName(),
                manager.isEnabled()
        );
    }


    @Transactional
    public BranchManagerResponse updateManager(
            Long managerId,
            BranchManagerUpdateRequest request) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found."));

        if (manager.getRole() != Role.BRANCH_MANAGER) {
            throw new IllegalArgumentException(
                    "The requested user is not a Branch Manager.");
        }

        manager.setName(request.getName());
        manager.setPhone(request.getPhone());
        manager.setAddress(request.getAddress());
        manager.setCity(request.getCity());
        manager.setState(request.getState());
        manager.setZip(request.getZip());

        User updatedManager = userRepository.save(manager);

        return mapToResponse(updatedManager);
    }

    @Transactional
    public BranchManagerResponse reassignManager(
            Long managerId,
            Long newBranchId) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found."));

        if (manager.getRole() != Role.BRANCH_MANAGER) {
            throw new IllegalArgumentException(
                    "The requested user is not a Branch Manager.");
        }

        Branch newBranch = branchRepository.findById(newBranchId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Branch not found."));

        if (!newBranch.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign manager to an inactive branch.");
        }

        manager.setBranch(newBranch);

        User updatedManager = userRepository.save(manager);

        return mapToResponse(updatedManager);
    }

}