package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

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

    @JsonIgnore
    private Instant loaded;

    public Field() {
    }

    public Field(String internalId, Integer position) {
        this.internalId = internalId;
        this.position = position;
        this.count = 0;
    }

}
