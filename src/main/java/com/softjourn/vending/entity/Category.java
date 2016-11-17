package com.softjourn.vending.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "Category name should not be null")
    @NotBlank(message = "Category name should not be blank and starts with symbols")
    @NotEmpty(message = "Category name should not be empty and starts with symbols")
    @Pattern(regexp = "^[a-zA-Z\\u0400-\\u04FF]+[ a-zA-Z\\u0400-\\u04FF]*[a-zA-Z\\u0400-\\u04FF]+$", message = "Category name should not contain numbers and starts with symbols")
    private String name;

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
