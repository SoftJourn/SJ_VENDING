package com.softjourn.vending.controller;


import com.softjourn.vending.entity.Product;
import com.softjourn.vending.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(method = RequestMethod.POST)
    public Product addProduct(@Valid @RequestBody Product product) {
        return productService.add(product);
    }

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

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public Product updateProduct(@PathVariable Integer id, @Valid @RequestBody Product updater) {
        return productService.update(id, updater);
    }

    @PreAuthorize("authenticated")
    @GetMapping("/category/{categoryName}")
    public List<Product> getProductsByCategory(@PathVariable String categoryName) {
        return productService.getProductsByCategory(categoryName);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/{id}/image", method = RequestMethod.GET)
    public byte[] getImage(@PathVariable Integer id) {
        return productService.getProduct(id).getImageData();
    }

    @RequestMapping(path = "/{id}/image", method = RequestMethod.POST, consumes = "multipart/form-data;charset=UTF-8")
    public void updateImage(@RequestParam MultipartFile file, @PathVariable Integer id) throws IOException {
        productService.updateImage(file, id);
    }

    @RequestMapping(path = "/{id}/description/image", method = RequestMethod.POST, consumes = "multipart/form-data;charset=UTF-8")
    public void addDescriptionImage(@RequestParam MultipartFile file, @PathVariable Integer id){

    }

    @RequestMapping(path = "/{id}/description/images", method = RequestMethod.GET, consumes = "multipart/form-data;charset=UTF-8")
    public void getAllDescriptionImage(@PathVariable Integer id){

    }

    @RequestMapping(path = "/description/image/{imageId}", method = RequestMethod.GET)
    public void deleteDescriptionImage(@PathVariable Integer imageId){

    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public Product deleteProduct(@PathVariable Integer id) {
        return productService.delete(id);
    }
}
