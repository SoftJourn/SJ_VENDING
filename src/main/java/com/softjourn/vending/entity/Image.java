package com.softjourn.vending.entity;

import static com.softjourn.vending.utils.Constants.IMAGE_FILE_MAX_SIZE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "images")
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
  private boolean isCover = false;

  @Override
  @JsonValue
  public String toString() {
    return this.url;
  }

  public Image(byte[] data, Integer productId, String resolution) {
    this.data = data;
    this.resolution = resolution;
    this.productId = productId;
  }
}
