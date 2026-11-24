
package com.rahim.crm.customer.service;

import com.rahim.crm.customer.dto.CustomerResponse;
import com.rahim.crm.customer.dto.RegisteredProductResponse;
import com.rahim.crm.customer.entity.Customer;
import com.rahim.crm.customer.repository.CustomerRepository;
import com.rahim.crm.customer.repository.RegisteredProductRepository;
import com.rahim.crm.customer.entity.RegisteredProduct;
import java.time.LocalDate;
import java.util.List;
import com.rahim.crm.customer.dto.CustomerUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerPortalService {

    private final CustomerRepository customerRepository;
    private final RegisteredProductRepository registeredProductRepository;


    public CustomerPortalService(
            CustomerRepository customerRepository,
            RegisteredProductRepository registeredProductRepository) {

        this.customerRepository = customerRepository;
        this.registeredProductRepository = registeredProductRepository;
    }

    @Transactional(readOnly = true)
    public CustomerResponse getMyProfile(Long userId) {

        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer profile not found"
                ));

        return mapToResponse(customer);
    }

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


    @Transactional
    public CustomerResponse updateMyProfile(
            Long userId,
            CustomerUpdateRequest request) {

        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer profile not found"
                ));

        String phone = request.getPhone().trim();
        String email = request.getEmail() == null
                ? null
                : request.getEmail().trim().toLowerCase();

        if (email != null && email.isBlank()) {
            email = null;
        }

        // Check whether another customer uses this phone.
        if (customerRepository.existsByPhoneAndIdNot(
                phone, customer.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phone number already exists"
            );
        }

        // Check email only when provided.
        if (email != null &&
                customerRepository.existsByEmailIgnoreCaseAndIdNot(
                        email, customer.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        customer.setFullName(request.getFullName().trim());
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(request.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }


    @Transactional(readOnly = true)
    public List<RegisteredProductResponse> getMyProducts(Long userId) {

        List<RegisteredProduct> products =
                registeredProductRepository.findByCustomer_User_Id(userId);

        return products.stream()
                .map(product -> {

                    String warrantyStatus;

                    if (product.getWarrantyExpiryDate() == null) {
                        warrantyStatus = "NOT_AVAILABLE";
                    } else if (!product.getWarrantyExpiryDate()
                            .isBefore(LocalDate.now())) {
                        warrantyStatus = "ACTIVE";
                    } else {
                        warrantyStatus = "EXPIRED";
                    }

                    return new RegisteredProductResponse(
                            product.getId(),
                            product.getSerialNumber(),
                            product.getProductName(),
                            product.getModel(),
                            product.getPurchaseDate(),
                            product.getWarrantyExpiryDate(),
                            warrantyStatus,
                            product.getCreatedAt()
                    );
                })
                .toList();
    }




}
