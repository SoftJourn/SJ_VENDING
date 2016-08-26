package com.softjourn.vending.service;


import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.utils.FileUploadUtil;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductService {

    private ProductRepository repository;

    private ReflectionMergeUtil<Product> mergeUtil;

    private static final String IMAGES_DIR = "images/";
    private String CONTEXT_PATH;

    @Autowired
    public ProductService(@NonNull ProductRepository repository, ServletContext servletContext) {
        this.repository = repository;
        String CONTEXT_PATH = servletContext.getRealPath("/images");

        mergeUtil = ReflectionMergeUtil
                .forClass(Product.class)
                .ignoreField("id")
                .ignoreNull(true)
                .build();
    }

    public Collection<Product> getProducts() {
        Iterable<Product> res = repository.findAll();
        return StreamSupport.stream(res.spliterator(), false).collect(Collectors.toList());
    }

    public Product getProduct(@NonNull Integer id) {
        Product product = repository.findOne(id);
        if (product == null) {
            throw new NotFoundException("Product with id " + id + " not found.");
        }
        return product;
    }

    public synchronized Product add(@NonNull Product product) {
        product.setAddedTime(Instant.now());
        return repository.save(product);
    }

    public synchronized Product update(@NonNull Integer id, @NonNull Product product) {
        Product old = getProduct(id);
        Product newProduct = mergeUtil.merge(old, product);
        repository.save(newProduct);
        return newProduct;
    }

    public synchronized void updateImage(@NonNull MultipartFile file, Integer id) {
        Product product = getProduct(id);
        String fileName = IMAGES_DIR + FileUploadUtil.saveImage(file, CONTEXT_PATH, product.getName(), product.getImageUrl());
        product.setImageUrl(fileName);
        repository.save(product);
    }

    public synchronized Product delete(@NonNull Integer id) {
        Product product = getProduct(id);
        repository.delete(id);
        return product;
    }

}
