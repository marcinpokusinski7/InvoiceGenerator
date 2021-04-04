package com.vaadin.invoice.repository;

import com.vaadin.invoice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("select c from Product c " +
            "where lower(c.productName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.productCategory) like lower(concat('%', :searchTerm, '%'))")
    List<Product> search(@Param("searchTerm") String filterText);
}
