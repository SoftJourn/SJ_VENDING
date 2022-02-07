package com.softjourn.vending.controller;

import com.softjourn.vending.entity.Product;
import com.softjourn.vending.service.ProductService;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
public class ProductsController {

  private final ProductService productService;

  // POST

  @PostMapping
  public Product addProduct(@Valid @RequestBody Product product) {
    return productService.add(product);
  }

  @PostMapping("/{id}")
  public Product updateProduct(@PathVariable Integer id, @Valid @RequestBody Product updater) {
    return productService.update(id, updater);
  }

  @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void updateCoverImage(
      @RequestParam MultipartFile file, @PathVariable Integer id
  ) throws IOException {
    productService.updateCoverImage(file, id);
  }

  @PostMapping(path = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void addProductImage(
      @RequestParam MultipartFile[] files, @PathVariable Integer id
  ) throws IOException {
    productService.addProductImage(files, id);
  }

  @PostMapping(path = "/{productId}/set/cover/{imgId}")
  public void setCoverByImgId(@PathVariable Integer productId, @PathVariable Long imgId) {
    productService.setCoverByImgId(productId, imgId);
  }

  // GET

  @GetMapping
  @PreAuthorize("authenticated")
  public Iterable<Product> getProducts() {
    return productService.getProducts();
  }

  @PreAuthorize("authenticated")
  @GetMapping("/category/{categoryName}")
  public List<Product> getProductsByCategory(@PathVariable String categoryName) {
    return productService.getProductsByCategory(categoryName);
  }

  @GetMapping("/search")
  public List<Product> getProductsByNameThatContain(@RequestParam("name") String name) {
    return productService.getProductsByNameThatContain(name);
  }

  @GetMapping("/{id}")
  @PreAuthorize("authenticated")
  public Product getProduct(@PathVariable Integer id) {
    return productService.getProduct(id);
  }

  @PreAuthorize("permitAll")
  @GetMapping(
      "/{productId}/images/{imageId:[\\d]+}{extension:(?:\\.(?:jpeg|png|jpg|apng|svg|bmp))?}")
  public byte[] getImage(@PathVariable Integer productId, @PathVariable Long imageId) {
    return productService.getImageById(productId, imageId);
  }

  // DELETE

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable Integer id) {
    productService.delete(id);
  }

  @DeleteMapping(
      "/{productId}/images/{imageId:[\\d]+}{extension:(?:\\.(?:jpeg|png|jpg|apng|svg|bmp))?}")
  public void deleteImage(@PathVariable Integer productId, @PathVariable Long imageId) {
    productService.deleteImage(productId, imageId);
  }
}
