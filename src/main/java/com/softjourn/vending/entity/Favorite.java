package com.softjourn.vending.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account", "product"})
})
public class Favorite {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String account;

    @ManyToOne
    private Product product;

    public Favorite(String account, Product product) {
        this.account = account;
        this.product = product;
    }
}
