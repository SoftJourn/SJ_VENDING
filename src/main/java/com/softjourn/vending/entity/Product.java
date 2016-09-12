package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    @NotNull
    @DecimalMin(value = "0", message = "Price can't be negative.")
    private BigDecimal price;

    @Column
    @NotBlank(message = "Name should be specified.")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonIgnore
    @Column(length = 2 * 1024 * 1024)
    private byte[] imageData;

    @Column
    @JsonIgnore
    private Instant addedTime;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categories")
    private Categories category;
}
