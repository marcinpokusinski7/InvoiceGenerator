package com.vaadin.invoice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
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
    @NotEmpty
    private String preparedBy;

    @Column(name = "date_created")
    @NotNull
    @NotEmpty
    private Date dateCreated;

    @Column(name = "future_payment")
    @NotNull
    @NotEmpty
    private Boolean futurePayment;

    @Column(name = "payment_date")
    @NotNull
    @NotEmpty
    private Date paymentDate;

    @Column(name = "client_name")
    @NotNull
    @NotEmpty
    private String clientName;

    @Column(name = "total_price")
    @NotNull
    @NotEmpty
    private Double totalPrice;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER)
    private List<Product> productList = new LinkedList<>();
}
