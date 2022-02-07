package com.softjourn.vending.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softjourn.vending.exceptions.ValueIsNotSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
  private Map<String, String> nutritionFacts;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  @OneToMany(mappedBy = "productId", fetch = FetchType.EAGER)
  private Set<Image> imageUrls = new HashSet<>();

  @JsonSetter(value = "imageUrls")
  public void setImageUrlsByJSON(Set imageUrls) {
//      Do nothing
  }

  @JsonIgnore
  @ElementCollection
  @JoinTable(name = "prices", joinColumns = @JoinColumn(name = "product_id"))
  @MapKeyColumn(name = "time")
  private Map<Instant, BigDecimal> prices = new HashMap<>();

  public Product(Integer id, BigDecimal price, String name, Instant addedTime, String description, Category category, Map<String, String> nutritionFacts, Set<Image> imageUrls) {
    this.id = id;
    this.price = price;
    this.prices.put(Instant.now(), price);
    this.name = name;
    this.addedTime = addedTime;
    this.description = description;
    this.category = category;
    this.nutritionFacts = nutritionFacts;
    this.imageUrls = imageUrls;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
    this.prices.put(Instant.now(), price);
  }

  @JsonIgnore
  public void setPrices(Instant time, BigDecimal price) {
    this.prices.put(time, price);
  }

  @JsonIgnore
  public Map<Instant, BigDecimal> getPrices() {
    return prices;
  }

  /**
   * Method gets latest price that was set
   *
   * @return BigDecimal
   */
  public BigDecimal getPrice() {
    Optional<Instant> last = prices.keySet().stream().sorted(Comparator.reverseOrder()).findFirst();
    return last.map(instant -> prices.get(instant)).orElseThrow(() -> new ValueIsNotSet("Product's price is not set"));
  }

  /**
   * Method gets first price that was set before entered date, if there is no such price then returns null
   *
   * @param time {@link Instant}
   * @return BigDecimal
   */
  public BigDecimal getPrice(Instant time) {
    Optional<Instant> first = prices.keySet().stream().filter(instant -> instant.isBefore(time)).sorted(Comparator.reverseOrder()).findFirst();
    return first.map(instant -> prices.get(instant)).orElse(null);
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

  @Override
  public String toString() {
    return "Product{" +
        "id=" + id +
        ", price=" + price +
        ", name='" + name + '\'' +
        ", addedTime=" + addedTime +
        ", description='" + description + '\'' +
        ", category=" + category +
        ", nutritionFacts=" + nutritionFacts +
        ", imageUrls=" + imageUrls +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Product product = (Product) o;

    if (!price.equals(product.price)) return false;
    if (!name.equals(product.name)) return false;
    return category.equals(product.category);
  }

  @Override
  public int hashCode() {
    int result = price.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + category.hashCode();
    return result;
  }
}
