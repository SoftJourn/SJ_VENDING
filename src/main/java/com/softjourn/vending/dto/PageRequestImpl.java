package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.softjourn.vending.utils.SortJsonDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestImpl {
    private int size;
    private int page;
    @JsonDeserialize(using = SortJsonDeserializer.class)
    private Sort sort;

    public Pageable toPageable() {
        if (sort == null) {
            return new PageRequest(page, size);
        } else {
            return new PageRequest(page, size, sort);
        }
    }
}
