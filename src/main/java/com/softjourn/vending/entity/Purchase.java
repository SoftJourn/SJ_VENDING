package com.softjourn.vending.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "purchases")
@Getter
@Setter
public class Purchase {

    @Id
    @GeneratedValue
    private Double id;

    @Column
    private String account;

    @ManyToOne
    private Product product;

    @Column
    private Instant time;
}
