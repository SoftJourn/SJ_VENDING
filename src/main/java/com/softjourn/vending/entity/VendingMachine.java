package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.dto.Size;
import com.softjourn.vending.utils.jsonview.View;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

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
}
