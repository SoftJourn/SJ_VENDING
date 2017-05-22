package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.softjourn.vending.utils.InstantJsonDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadHistoryRequestDTO {

    @NotNull(message = "Machine id should be set")
    private Integer machineId;

    @JsonDeserialize(using = InstantJsonDeserializer.class)
    private Instant start;

    @JsonDeserialize(using = InstantJsonDeserializer.class)
    private Instant due;

    @NotNull(message = "Pageable should be set")
    private PageRequestImpl pageable;

    public Pageable getPageable() {
        return pageable.toPageable();
    }
}
