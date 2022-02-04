package com.softjourn.vending.entity;

import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@Builder
@NoArgsConstructor
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

  public Purchase(
      String account, String productName, BigDecimal productPrice,
      VendingMachine machine, Instant time
  ) {
    this.account = account;
    this.productName = productName;
    this.productPrice = productPrice;
    this.machine = machine;
    this.time = time;
  }
}
