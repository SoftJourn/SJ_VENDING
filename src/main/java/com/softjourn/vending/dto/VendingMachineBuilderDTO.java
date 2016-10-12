package com.softjourn.vending.dto;


import lombok.Data;

@Data
public class VendingMachineBuilderDTO {

    private String name;

    private Integer rowsCount;

    private Integer columnsCount;

    private Numbering rowsNumbering;

    private Numbering columnsNumbering;

    public enum Numbering {
        NUMERICAL, ALPHABETICAL, CUSTOM
    }
}

