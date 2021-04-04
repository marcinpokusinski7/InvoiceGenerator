package com.vaadin.invoice.service;

import com.vaadin.invoice.entity.Product;
import com.vaadin.invoice.repository.InvoiceRepository;
import com.vaadin.invoice.repository.ProductRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Data
@NoArgsConstructor
public class ProductService {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private ProductRepository productRepository;
    private InvoiceRepository invoiceRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, InvoiceRepository invoiceRepository) {
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public List<Product> findAll(String filterText){
        if(filterText == null || filterText.isEmpty()){
            return productRepository.findAll();
        }else {
            return productRepository.search(filterText);
        }
    }

    public long count(){
        return productRepository.count();
    }

    public void delete(Product product){
        productRepository.delete(product);
    }

    public void save(Product product){
        if(product == null){
            LOGGER.log(Level.SEVERE,"Invoice is null");
            return;
        }
        productRepository.save(product);
    }

}
