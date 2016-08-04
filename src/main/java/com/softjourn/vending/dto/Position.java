package com.softjourn.vending.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
    int row;
    int column;
    String cellName;
}
