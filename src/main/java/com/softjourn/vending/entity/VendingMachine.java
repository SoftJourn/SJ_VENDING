package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.dto.Size;
import com.softjourn.vending.utils.jsonview.View;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "machines")
public class VendingMachine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.Client.class)
  private Integer id;

  @Column(unique = true)
  @JsonView(View.Client.class)
  private String name;

  @Column
  private String url;

  @Column(unique = true)
  private String uniqueId;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "machine_rows",
      joinColumns = @JoinColumn(name = "machine"),
      inverseJoinColumns = @JoinColumn(name = "row"))
  private List<Row> rows;

  @NotNull
  @JsonIgnore
  private Integer cellLimit;
  private boolean isActive;

  @Column(name = "is_virtual")
  private boolean isVirtual;

  @JsonIgnore
  public List<Field> getFields() {
    return rows.stream()
        .flatMap(r -> r.getFields().stream())
        .collect(Collectors.toList());
  }

  //TODO Works correctly only for "rectangular" machines.
  @JsonView(View.Client.class)
  public Size getSize() {
    int rowsCount = rows.size();
    int columnsCount = rows.get(0).getFields().size();
    return new Size(rowsCount, columnsCount, cellLimit);
  }


  public boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(boolean active) {
    isActive = active;
  }

  public boolean getIsVirtual() {
    return isVirtual;
  }

  public void setIsVirtual(boolean virtual) {
    isVirtual = virtual;
  }
}
