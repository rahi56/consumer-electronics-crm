
package com.rahim.crm.branch.service;

import com.rahim.crm.branch.dto.BranchRequest;
import com.rahim.crm.branch.dto.BranchResponse;
import com.rahim.crm.branch.entity.Branch;
import com.rahim.crm.branch.repository.BranchRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    // Create a new branch
    public BranchResponse createBranch(BranchRequest request) {

        if (branchRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Branch code already exists"
            );
        }

        Branch branch = new Branch();

        branch.setName(request.getName());
        branch.setCode(request.getCode());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setPostalCode(request.getPostalCode());

        Branch savedBranch = branchRepository.save(branch);

        return mapToResponse(savedBranch);
    }

    // Get all branches
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {

        return branchRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get branch by ID
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found with ID: " + id
                ));

        return mapToResponse(branch);
    }

    // Update branch information
    public BranchResponse updateBranch(Long id, BranchRequest request) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found with ID: " + id
                ));

        branchRepository.findByCode(request.getCode())
                .ifPresent(existingBranch -> {
                    if (!existingBranch.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Branch code already exists"
                        );
                    }
                });

        branch.setName(request.getName());
        branch.setCode(request.getCode());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setPostalCode(request.getPostalCode());

        Branch updatedBranch = branchRepository.save(branch);

        return mapToResponse(updatedBranch);
    }

    // Deactivate branch
    public BranchResponse deactivateBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found with ID: " + id
                ));

        branch.setActive(false);

        Branch updatedBranch = branchRepository.save(branch);

        return mapToResponse(updatedBranch);
    }

    // Reactivate branch
    public BranchResponse activateBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found with ID: " + id
                ));

        branch.setActive(true);

        Branch updatedBranch = branchRepository.save(branch);

        return mapToResponse(updatedBranch);
    }

    // Convert Entity into Response DTO
    private BranchResponse mapToResponse(Branch branch) {

        return new BranchResponse(
                branch.getId(),
                branch.getName(),
                branch.getCode(),
                branch.getPhone(),
                branch.getEmail(),
                branch.getAddress(),
                branch.getCity(),
                branch.getState(),
                branch.getPostalCode(),
                branch.isActive(),
                branch.getCreatedAt(),
                branch.getUpdatedAt()
        );
    }


}
