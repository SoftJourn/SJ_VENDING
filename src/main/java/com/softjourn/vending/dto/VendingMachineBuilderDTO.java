package com.softjourn.vending.dto;


import lombok.Data;

@Data
public class VendingMachineBuilderDTO {

    private String name;

    private String url;

    private Integer rowsCount;

    private Integer columnsCount;

    private Numbering rowsNumbering;

    private Numbering columnsNumbering;

    private Integer productsInCellLimit;

    private Boolean isActive;

    public enum Numbering {
        NUMERICAL, ALPHABETICAL, CUSTOM
    }
}

