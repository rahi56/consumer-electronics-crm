
package com.rahim.crm.customer.service;

import com.rahim.crm.customer.dto.CustomerRequest;
import com.rahim.crm.customer.dto.CustomerUpdateRequest;
import com.rahim.crm.customer.dto.CustomerResponse;
import com.rahim.crm.customer.entity.Customer;
import com.rahim.crm.customer.repository.CustomerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // CREATE CUSTOMER
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phone number already registered"
            );
        }

        if (request.getEmail() != null &&
                !request.getEmail().isBlank() &&
                customerRepository.existsByEmailIgnoreCase(request.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered"
            );
        }

        Customer customer = new Customer();

        customer.setCustomerCode(generateCustomerCode());
        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    // UPDATE CUSTOMER
    @Transactional
    public CustomerResponse updateCustomer(
            Long id,
            CustomerUpdateRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        // Check duplicate phone
        if (customerRepository.existsByPhoneAndIdNot(
                request.getPhone(), id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phone number already registered with another customer"
            );
        }

        // Check duplicate email
        if (request.getEmail() != null &&
                !request.getEmail().isBlank() &&
                customerRepository.existsByEmailIgnoreCaseAndIdNot(
                        request.getEmail(), id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered with another customer"
            );
        }

        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    // GET CUSTOMER BY ID
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        return mapToResponse(customer);
    }

    // GET ALL CUSTOMERS
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GENERATE CUSTOMER CODE
    private String generateCustomerCode() {

        String customerCode;

        do {
            customerCode = "CUS-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
        } while (customerRepository.existsByCustomerCode(customerCode));

        return customerCode;
    }

    // ENTITY TO RESPONSE DTO
    private CustomerResponse mapToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getFullName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.isActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search keyword cannot be empty"
            );
        }

        String search = keyword.trim();

        return customerRepository
                .findByCustomerCodeContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(
                        search,
                        search,
                        search
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Transactional
    public String deactivateCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        if (!customer.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer is already inactive"
            );
        }

        customer.setActive(false);

        customerRepository.save(customer);

        return "Customer deactivated successfully";
    }


    @Transactional
    public String activateCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        if (customer.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer is already active"
            );
        }

        customer.setActive(true);

        customerRepository.save(customer);

        return "Customer activated successfully";
    }


}
