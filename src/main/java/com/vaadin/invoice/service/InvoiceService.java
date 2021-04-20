package com.vaadin.invoice.service;

import com.vaadin.invoice.entity.Invoice;
import com.vaadin.invoice.repository.InvoiceRepository;
import com.vaadin.invoice.repository.ProductRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Data
@NoArgsConstructor
public class InvoiceService {
    private static final Logger LOGGER = Logger.getLogger(InvoiceService.class.getName());
    private ProductRepository productRepository;
    private InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceService(ProductRepository productRepository, InvoiceRepository invoiceRepository) {
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @PersistenceContext
    EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public List<Invoice> findAll(){
        return invoiceRepository.findAll();
    }

    public long count(){
        return invoiceRepository.count();
    }

    public void delete(Invoice invoice){
        invoiceRepository.delete(invoice);
    }

    public void save(Invoice invoice){
        if(invoice == null){
        LOGGER.log(Level.SEVERE,"Invoice is null");
              return;
        }
        invoiceRepository.save(invoice);
    }

}
