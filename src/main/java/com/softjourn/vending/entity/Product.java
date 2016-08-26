package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
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

    @Column
    @JsonIgnore
    private Instant addedTime;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    public enum Category {
        DRINK("Drink"),
        SNACK("Snack");

        private String readableName;

        public String readableName() {
            return readableName;
        }

        Category(String readableName) {
            this.readableName = readableName;
        }
    }
}
