package com.vaadin.invoice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
public class Invoice implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer invoiceId;

    @Column(name = "prepared_by")
    @NotNull
    private String preparedBy;

    @Column(name = "date_created")
    @NotNull
    private LocalDate dateCreated;

    @Column(name = "future_payment")
    @NotNull
    private String futurePayment;

    @Column(name = "payment_date")
    @NotNull
    private LocalDate paymentDate;

    @Column(name = "client_name")
    @NotNull
    private String clientName;

    @Column(name = "total_price")
    @NotNull
    private Double totalPrice;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER)
    private List<Product> productList = new LinkedList<>();
}
