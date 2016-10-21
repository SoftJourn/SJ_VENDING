package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "load_history")
public class LoadHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal price;

    @Column(name = "date_added")
    private Instant dateAdded;

    @Column(name = "is_distributed")
    private Boolean isDistributed;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private VendingMachine vendingMachine;
}
