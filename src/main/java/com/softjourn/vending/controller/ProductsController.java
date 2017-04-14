package com.softjourn.vending.controller;


import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.service.ProductImageService;
import com.softjourn.vending.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private final ProductImageService imageService;
    private ProductService productService;

    @Autowired
    public ProductsController(ProductService productService, ProductImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
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
    public List<ProductImage> addProductImages(@RequestParam MultipartFile files[], @PathVariable Integer id) throws IOException {
        return imageService.add(files, id);
    }

    @RequestMapping(path = "/{productId}/set/cover/{imgId}", method = RequestMethod.POST)
    public void setCoverByImgId(@PathVariable Integer productId, @PathVariable Long imgId) throws IOException {
        productService.setCoverByImgId(productId, imgId);
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
    @RequestMapping(path = "/{productId}/images/{imageName:.*}", method = RequestMethod.GET)
    public byte[] getImage(@PathVariable Integer productId, @PathVariable String imageName) throws IOException {
        String uri = String.format("/%s/images/%s", productId, imageName);
        return imageService.get(uri);
    }

    // DELETE

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void deleteProduct(@PathVariable Integer id) {
        productService.delete(id);
    }

    @RequestMapping(path = "/{productId}/images/{imageName:.*}", method = RequestMethod.DELETE)
    public void deleteImage(@PathVariable Integer productId, @PathVariable String imageName) throws IOException {
        String uri = String.format("/%s/images/%s", productId, imageName);
        imageService.delete(uri);
    }

    // ALL

    @PreAuthorize("authenticated")
    @GetMapping("/category/{categoryName}")
    public List<Product> getProductsByCategory(@PathVariable String categoryName) {
        return productService.getProductsByCategory(categoryName);
    }
}
