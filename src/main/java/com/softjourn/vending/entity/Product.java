package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

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
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;

import static com.softjourn.vending.utils.Constants.IMAGE_FILE_MAX_SIZE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    @NotNull
    @DecimalMin(value = "0", message = "Price should be positive")
    private BigDecimal price;

    @Column
    @NotNull(message = "Product name should not be null")
    @NotBlank(message = "Product name should not be blank and starts with symbols")
    @NotEmpty(message = "Product name should not be empty and starts with symbols")
    @Pattern(regexp = "^[a-zA-Z0-9\\u0400-\\u04FF]+[ a-zA-Z0-9\\u0400-\\u04FF]*[a-zA-Z0-9\\u0400-\\u04FF]+", message = "Product name should't starts and ends with whitespaces and should't contain special characters")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonIgnore
    @Column(length = IMAGE_FILE_MAX_SIZE)
    private byte[] imageData;

    @Column
    @JsonIgnore
    private Instant addedTime;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categories")
    @NotNull(message = "Product category is required")
    private Categories category;
}
