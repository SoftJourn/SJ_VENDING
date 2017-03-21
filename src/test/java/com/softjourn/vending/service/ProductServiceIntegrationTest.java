package com.softjourn.vending.service;

import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.dao.ImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Image;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.ProductNotFoundException;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@DataJpaTest
@Log
public class ProductServiceIntegrationTest {

    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Mock
    private FavoritesRepository favoritesRepository;

    @Mock
    private MultipartFile imagePng;
    @Mock
    private MultipartFile imageJpg;

    private Integer testProductId = 0;

    @Test
    public void getProduct_WithMultipleImages() throws Exception {
        int productId = this.testProductId;
        Image storedImage = this.productService.addProductImage(this.imagePng, productId);
        String result = "[products/" + productId + "/images/" + storedImage.getId() + ".png]";
        Product product = this.productService.getProduct(productId);
        assertNotNull(product.getImageUrls());
        assertEquals(result, product.getImageUrls().toString());
        log.info(product.getImageUrls().toString());
    }

    @Test
    public void addImage() throws Exception {
        this.productService.addProductImage(this.imageJpg, testProductId);
        this.productService.addProductImage(this.imagePng, testProductId);
        Product product;
        product = this.productRepository.findOne(testProductId);
        assertNotNull(product.getImageUrls());
    }

    @Test(expected = ProductNotFoundException.class)
    public void addImage_NotExistedProduct() throws Exception {
        this.productService.addProductImage(this.imageJpg, Integer.MAX_VALUE);
    }

    @Test
    public void addProductImage_getAllProducts_integration() throws Exception {
        this.productService.addProductImage(this.imagePng, testProductId);

        List<Image> images = this.imageRepository.findByProductId(testProductId);
        assertEquals(1, images.size());

        List<Product> allProducts = this.productService.getProducts();
        // Finding products with product.id and don`t empty images
        long count = allProducts
            .stream()
            .filter(product -> Objects.equals(product.getId(), testProductId))
            .filter(product -> !product.getImageUrls().isEmpty())
            .count();
        assertEquals(1,count);
    }

    @Test
    public void getImageUrl() throws Exception {

    }

    @Before
    public void setUp() throws Exception {

        productService = new ProductService(productRepository, favoritesRepository, imageRepository);

        byte[] imageData = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 45, 78, 56, 45, 12, 5, 48, 7, 54, 21, 5, 45, 4, 87, 8,
            75, 41, 21, 51};
        byte[] realImage = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 17, 0, 0, 0,
            18, 8, 6, 0, 0, 0, -67, -7, 53, 84, 0, 0, 0, 4, 115, 66, 73, 84, 8, 8, 8, 8, 124, 8, 100, -120, 0, 0, 0,
            25, 116, 69, 88, 116, 83, 111, 102, 116, 119, 97, 114, 101, 0, 103, 110, 111, 109, 101, 45, 115, 99, 114,
            101, 101, 110, 115, 104, 111, 116, -17, 3, -65, 62, 0, 0, 0, 31, 73, 68, 65, 84, 56, -115, 99, -4, -1, -1,
            -1, 127, 6, 10, 1, 19, -91, 6, -116, 26, 50, 106, -56, -88, 33, -93, -122, 80, -45, 16, 0, -89, -19, 4, 32,
            -98, -16, 34, -99, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};

        when(imagePng.getContentType()).thenReturn("image/png");
        when(imageJpg.getContentType()).thenReturn("image/jpeg");

        when(imagePng.getBytes()).thenReturn(imageData);
        when(imagePng.getBytes()).thenReturn(imageData);
        when(imagePng.getInputStream()).thenReturn(new ByteArrayInputStream(realImage));
        when(imageJpg.getInputStream()).thenReturn(new ByteArrayInputStream(realImage));
    }

}