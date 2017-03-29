package com.softjourn.vending.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String account;

    @Column
    private String productName;

    @Column
    private BigDecimal productPrice;

    @Column
    private Instant time;

    @ManyToOne
    @JoinColumn(name = "machine")
    private VendingMachine machine;

    public Purchase(String account, String productName, BigDecimal productPrice, VendingMachine machine, Instant time) {
        this.account = account;
        this.productName = productName;
        this.productPrice = productPrice;
        this.machine = machine;
        this.time = time;
    }
}
