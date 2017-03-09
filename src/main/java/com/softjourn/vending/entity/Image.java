package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.softjourn.vending.utils.Constants.IMAGE_FILE_MAX_SIZE;

@Entity
@Data
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    @Column(length = IMAGE_FILE_MAX_SIZE)
    private byte[] data;

    private String resolution;

    private int productId;

    public Image(byte[] bytes, int productId, String resolution) {
        this.data = bytes;
        this.productId = productId;
        this.resolution = resolution;
    }
}
