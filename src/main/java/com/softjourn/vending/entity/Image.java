package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.utils.jsonview.View;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Arrays;

import static com.softjourn.vending.utils.Constants.IMAGE_FILE_MAX_SIZE;

@Entity
@Data
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @Column(length = IMAGE_FILE_MAX_SIZE)
    private byte[] data;

    private String url;
    private String resolution;
    private Integer productId;
    private Boolean isCover = false;

    @Override
    @JsonValue
    public String toString() {
        return this.url;
    }

    //  TODO  3. Migrate cover image from product here.

    public Image(byte[] data, Integer productId, String resolution) {
        this.data = data;
        this.resolution = resolution;
        this.productId = productId;
    }
}
