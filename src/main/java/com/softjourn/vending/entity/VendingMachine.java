package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.dto.Size;
import com.softjourn.vending.utils.jsonview.View;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "machines")
public class VendingMachine {

    @Id
    @GeneratedValue
    @JsonView(View.Client.class)
    private Integer id;

    @Column
    @JsonView(View.Client.class)
    private String name;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "machine_rows",
            joinColumns = @JoinColumn(name = "machine"),
            inverseJoinColumns = @JoinColumn(name = "row"))
    private List<Row> rows;

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
        return new Size(rowsCount, columnsCount);
    }
}
