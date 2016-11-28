package com.softjourn.vending.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.utils.jsonview.View;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Size {

    @JsonView(View.Client.class)
    private int rows;

    @JsonView(View.Client.class)
    private int columns;

    @JsonView(View.Client.class)
    private int cellLimit;
}
