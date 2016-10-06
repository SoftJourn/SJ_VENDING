package com.softjourn.vending.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String account;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    @Column
    private Instant time;

    @ManyToOne
    @JoinColumn(name = "machine")
    private VendingMachine machine;

    public Purchase(String account, Product product, VendingMachine machine, Instant time) {
        this.account = account;
        this.product = product;
        this.machine = machine;
        this.time = time;
    }
}
