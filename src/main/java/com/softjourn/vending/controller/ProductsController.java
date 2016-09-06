package com.softjourn.vending.controller;


import com.softjourn.vending.entity.Product;
import com.softjourn.vending.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/products")
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

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Product> getProducts() {
        return productService.getProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Product getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public Product updateProduct(@PathVariable Integer id, @Valid @RequestBody Product updater) {
        return productService.update(id, updater);
    }

    @RequestMapping(path = "/{id}/image", method = RequestMethod.GET)
    public byte[] getImage(@PathVariable Integer id) {
        return productService.getProduct(id).getImageData();
    }

    @RequestMapping(path = "/{id}/image", method = RequestMethod.POST, consumes = "multipart/form-data;charset=UTF-8")
    public void updateImage(@RequestParam MultipartFile file, @PathVariable Integer id) {
        productService.updateImage(file, id);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public Product deleteProduct(@PathVariable Integer id) {
        return productService.delete(id);
    }
}
