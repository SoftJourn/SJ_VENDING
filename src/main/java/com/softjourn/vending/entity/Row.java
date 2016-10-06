package com.softjourn.vending.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rows")
public class Row {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "row_id")
    private String rowId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "row_fields",
            joinColumns = @JoinColumn(name = "row"),
            inverseJoinColumns = @JoinColumn(name = "field"))
    private List<Field> fields;

    public Row(String rowId) {
        this.rowId = rowId;
    }

    public List<Field> getFields() {
        return fields.stream()
                .sorted((f1, f2) -> f1.getPosition() - f2.getPosition())
                .collect(Collectors.toList());
    }

    public void adField(Field field) {
        fields.add(field);
    }

    public void removeFields(Collection<Field> fields) {
        this.fields.removeAll(fields);
    }
}
