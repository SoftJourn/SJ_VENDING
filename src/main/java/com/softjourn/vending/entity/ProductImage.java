package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.entity.listeners.ProductImageListener;
import com.softjourn.vending.utils.jsonview.View;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Arrays;

import static com.softjourn.vending.utils.Constants.IMAGE_FILE_MAX_SIZE;

@Entity
@Data
@NoArgsConstructor
@Table(name = "images")
@Builder
@AllArgsConstructor
@EntityListeners(ProductImageListener.class)
public class ProductImage {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @Column(length = IMAGE_FILE_MAX_SIZE)
    private byte[] data;

    private String url;
    private String resolution;
    // TODO remove product id
    private Integer productId;
    private boolean isCover = false;

    public ProductImage(byte[] data, Integer productId, String resolution) {
        this.data = data;
        this.resolution = resolution;
        this.productId = productId;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.url;
    }
}
