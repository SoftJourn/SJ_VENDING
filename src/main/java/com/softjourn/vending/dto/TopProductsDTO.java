package com.softjourn.vending.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductsDTO {

    @NotNull(message = "Should not be null")
    @DecimalMin(value = "0", message = "Size should be positive")
    private Integer topSize;

    @NotNull(message = " Start should not be null")
    @NotEmpty(message = "Start should not be empty")
    @NotBlank(message = "Start should not be blank")
    private String start;

    @NotNull(message = "Due should not be null")
    @NotEmpty(message = "Due should not be empty")
    @NotBlank(message = "Due should not be blank")
    private String due;

}
