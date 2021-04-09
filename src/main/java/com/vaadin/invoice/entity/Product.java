package com.vaadin.invoice.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
public class Product implements Cloneable {

    public enum Status {
        Books, Electronic, Games, Laptops, TV
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotEmpty
    @NotNull
    @Column(name = "product_name")
    private String productName;

    @NotEmpty
    @NotNull
    @Column(name = "price")
    private Double price;

    @NotEmpty
    @NotNull
    @Column(name = "in_stock")
    private Integer inStock;

    @NotNull
    @Enumerated(EnumType.STRING)  //helps to provide list of prepared enums to choose for generating fv
    @Column(name = "product_Category")
    private Product.Status productCategory;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}