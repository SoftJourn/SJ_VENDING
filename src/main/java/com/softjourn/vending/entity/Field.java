package com.softjourn.vending.entity;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="internal_id")
    @NotBlank(message = "FieldId should be specified.")
    private String internalId;

    private Integer position;

    @Column(name="count")
    @NotNull
    @DecimalMin(value = "0", message = "Count can't be negative.")
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    public Field() {
    }

    public Field(String internalId, Integer position) {
        this.internalId = internalId;
        this.position = position;
        this.count = 0;
    }

}
