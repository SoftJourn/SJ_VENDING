package com.softjourn.vending.service;


import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.exceptions.NotImageException;
import com.softjourn.vending.exceptions.WrongImageDimensions;
import com.softjourn.vending.utils.FileUploadUtil;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_HEIGHT;
import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_WIDTH;

@Service
@Slf4j
public class ProductService {

    private ProductRepository productRepository;

    private ReflectionMergeUtil<Product> mergeUtil;

    @Autowired
    public ProductService(@NonNull ProductRepository productRepository, ServletContext servletContext) {
        this.productRepository = productRepository;

        mergeUtil = ReflectionMergeUtil
                .forClass(Product.class)
                .ignoreField("id")
                .ignoreNull(true)
                .build();
    }

    public Collection<Product> getProducts() {
        Iterable<Product> res = productRepository.findAll();
        return StreamSupport.stream(res.spliterator(), false).collect(Collectors.toList());
    }

    public List<Product> getProductsByNameThatContain(String name) {
        return productRepository.getProductsByNameThatContain(name);
    }

    public Product getProduct(@NonNull Integer id) {
        Product product = productRepository.findOne(id);
        if (product == null) {
            throw new NotFoundException("Product with id " + id + " not found.");
        }
        return product;
    }

    public synchronized Product add(@NonNull Product product) {
        product.setAddedTime(Instant.now());
        return productRepository.save(product);
    }

    public synchronized Product update(@NonNull Integer id, @NonNull Product product) {
        Product old = getProduct(id);
        Product newProduct = mergeUtil.merge(old, product);
        productRepository.save(newProduct);
        return newProduct;
    }

    @Transactional
    public synchronized void updateImage(@NonNull MultipartFile file, Integer id) throws IOException {
        this.validateImageMimeType(file);
        this.validateImageDimensions(ImageIO.read(file.getInputStream()));
        Product product = getProduct(id);
        setImage(product, file);
        productRepository.save(product);
    }

    public synchronized Product delete(@NonNull Integer id) {
        Product product = getProduct(id);
        productRepository.delete(id);
        return product;
    }

    private Product setImage(Product product, MultipartFile image) {
        try {
            String resolution = FileUploadUtil.getResolution(image);
            product.setImageData(image.getBytes());
            product.setImageUrl("products/" + product.getId() + "/image." + resolution);
            return product;
        } catch (IOException e) {
            throw new RuntimeException("Can't save image for product with id " + product.getId(), e);
        }
    }

    private void validateImageMimeType(MultipartFile file) {
        if (file.getContentType().matches("image/(?:jpeg|png|jpg|apng|svg|bmp)")) {
        } else {
            throw new NotImageException("File is not image");
        }
    }

    private void validateImageDimensions(BufferedImage image) {
        if (image.getWidth() > IMAGE_DIMENSIONS_MAX_WIDTH || image.getHeight() > IMAGE_DIMENSIONS_MAX_HEIGHT) {
            throw new WrongImageDimensions("Wrong image dimensions");
        }
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.getProductByCategory_Name(categoryName);
    }
}
