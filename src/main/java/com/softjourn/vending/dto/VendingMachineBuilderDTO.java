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

    private Boolean isActive;

    private Boolean isVirtual;

    private Integer cellLimit;

    public enum Numbering {
        NUMERICAL, ALPHABETICAL, CUSTOM
    }
}

