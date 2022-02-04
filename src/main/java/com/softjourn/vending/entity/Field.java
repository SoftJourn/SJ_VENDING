package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "fields")
@NoArgsConstructor
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

  public Field(String internalId, Integer position) {
    this.internalId = internalId;
    this.position = position;
    this.count = 0;
  }
}
