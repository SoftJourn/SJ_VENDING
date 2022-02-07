package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "load_history")
public class LoadHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private BigDecimal price;

  @Column
  private BigDecimal total;

  @Column(name = "date_added")
  private Instant dateAdded;

  // load identifier(to differentiate loads)
  @JsonIgnore
  private String hash;

  @Column(name = "is_distributed")
  private Boolean isDistributed;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "machine_id")
  private VendingMachine vendingMachine;

  @ManyToOne
  @JoinColumn(name = "product")
  @NotNull(message = "Product is required")
  private Product product;

  @ManyToOne
  @JoinColumn(name = "field")
  @NotNull(message = "Field is required")
  private Field field;

  @Column
  @NotNull(message = "Count is required")
  @Min(value = 0, message = "Count should be positive")
  private Integer count;

  /**
   * Method converts Instant to java.util.Date with zone id
   * Needed only for excel reports - apache poi
   *
   * @param timeZone
   * @return Date
   * @throws ParseException
   */
  public String dateAddedToDate(TimeZone timeZone) throws ParseException {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    timeFormatter.withZone(timeZone.toZoneId());
    TemporalAccessor accessor = timeFormatter.parse(dateAdded.toString());
    return Date.from(Instant.from(accessor)).toString();
  }
}
