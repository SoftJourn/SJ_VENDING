package com.softjourn.vending.dto;


import lombok.Data;

@Data
public class VendingMachineBuilderDTO {

    private String name;

    private String address;

    private Integer rowsCount;

    private Integer columnsCount;

    private Numbering rowsNumbering;

    private Numbering columnsNumbering;

    public enum Numbering {
        NUMERICAL, ALPHABETICAL, CUSTOM
    }
}

