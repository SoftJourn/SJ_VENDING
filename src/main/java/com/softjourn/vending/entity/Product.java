package com.softjourn.vending.entity;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @DecimalMin(value = "0", message = "Price should be positive")
    private BigDecimal price;

    @NotNull(message = "Product name should not be null")
    @NotBlank(message = "Product name should not be blank and starts with symbols")
    @NotEmpty(message = "Product name should not be empty and starts with symbols")
    @Pattern(regexp = "^[a-zA-Z0-9\\u0400-\\u04FF]+[ a-zA-Z0-9\\u0400-\\u04FF,-]*[a-zA-Z0-9\\u0400-\\u04FF,-]+", message = "Product name should't starts and ends with whitespaces and should't contain special characters")
    private String name;

    @JsonIgnore
    private Instant addedTime;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categories")
    @NotNull(message = "Product category is required")
    private Category category;

    @ElementCollection
    private Map<String,String> nutritionFacts;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @OneToMany(mappedBy = "productId", fetch = FetchType.EAGER)
    private Set<Image> imageUrls = new HashSet<>();

    @JsonSetter(value = "imageUrls")
    public void setImageUrlsByJSON(Set imageUrls){
//      Do nothing
    }

    @JsonGetter
    public String getImageUrl() {
        if (this.imageUrls != null)
            return this.imageUrls
                .stream()
                .filter(Image::isCover)
                .findFirst()
                .map(Image::getUrl)
                .orElse("");
        else
            return "";
    }

}
