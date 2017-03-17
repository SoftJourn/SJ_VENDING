package com.softjourn.vending.controller;


import com.softjourn.vending.entity.Product;
import com.softjourn.vending.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
public class ProductsController {

    private ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    // POST

    @RequestMapping(method = RequestMethod.POST)
    public Product addProduct(@Valid @RequestBody Product product) {
        return productService.add(product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public Product updateProduct(@PathVariable Integer id, @Valid @RequestBody Product updater) {
        return productService.update(id, updater);
    }

    @RequestMapping(path = "/{id}/image", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateCoverImage(@RequestParam MultipartFile file, @PathVariable Integer id) throws IOException {
        productService.updateCoverImage(file, id);
    }

    @RequestMapping(path = "/{id}/images", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addProductImage(@RequestParam MultipartFile files[], @PathVariable Integer id) throws IOException {
        productService.addProductImage(files, id);
    }

    // GET

    @PreAuthorize("authenticated")
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Product> getProducts() {
        return productService.getProducts();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Product> getProductsByNameThatContain(@RequestParam("name") String name) {
        return productService.getProductsByNameThatContain(name);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Product getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/{id}/image", method = RequestMethod.GET)
    public byte[] getImage(@PathVariable Integer id) {
        return productService.getProduct(id).getImageData();
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/{productId}/images/{imageId}", method = RequestMethod.GET)
    public byte[] getImage(@PathVariable Integer productId, @PathVariable Long imageId) {
        return productService.getImageById(productId, imageId);
    }

    // DELETE

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public Product deleteProduct(@PathVariable Integer id) {
        return productService.delete(id);
    }

    @RequestMapping(path = "/{productId}/images/{imageId}", method = RequestMethod.DELETE)
    public void deleteImage(@PathVariable Integer productId, @PathVariable Long imageId) {
        productService.deleteImage(productId, imageId);
    }

    // ALL

    @PreAuthorize("authenticated")
    @GetMapping("/category/{categoryName}")
    public List<Product> getProductsByCategory(@PathVariable String categoryName) {
        return productService.getProductsByCategory(categoryName);
    }
}
