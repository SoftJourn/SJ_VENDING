package com.softjourn.vending.dto;

import com.softjourn.vending.annotation.ValidDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidDate
public class PurchaseFilterDTO {

    @NotNull(message = "Machine's id should not be null")
    private Integer machineId;

    @NotNull(message = "Type should not be null")
    @NotBlank(message = "Type should not be blank")
    @NotEmpty(message = "Type should not be empty")
    private String type;

    @NotNull(message = "TimeZoneOffSet should not be null")
    private Integer timeZoneOffSet;

    private String start;

    private String due;

}
