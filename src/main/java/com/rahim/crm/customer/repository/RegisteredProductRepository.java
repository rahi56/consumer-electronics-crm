
package com.rahim.crm.customer.repository;

import com.rahim.crm.customer.entity.RegisteredProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisteredProductRepository
        extends JpaRepository<RegisteredProduct, Long> {

    List<RegisteredProduct> findByCustomer_User_Id(Long userId);
}
