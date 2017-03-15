package com.softjourn.vending.service;


import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.dao.ImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Category;
import com.softjourn.vending.entity.Image;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)

@Log
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private FavoritesRepository favoritesRepository;

    private ProductService productService;

    @Mock
    private MultipartFile imagePng;
    @Mock
    private MultipartFile imageJpg;

    private byte[] imageData = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 45, 78, 56, 45, 12, 5, 48, 7, 54, 21, 5, 45, 4, 87, 8,
        75, 41, 21, 51};

    private byte[] realImage = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 17, 0, 0, 0,
        18, 8, 6, 0, 0, 0, -67, -7, 53, 84, 0, 0, 0, 4, 115, 66, 73, 84, 8, 8, 8, 8, 124, 8, 100, -120, 0, 0, 0,
        25, 116, 69, 88, 116, 83, 111, 102, 116, 119, 97, 114, 101, 0, 103, 110, 111, 109, 101, 45, 115, 99, 114,
        101, 101, 110, 115, 104, 111, 116, -17, 3, -65, 62, 0, 0, 0, 31, 73, 68, 65, 84, 56, -115, 99, -4, -1, -1,
        -1, 127, 6, 10, 1, 19, -91, 6, -116, 26, 50, 106, -56, -88, 33, -93, -122, 80, -45, 16, 0, -89, -19, 4, 32,
        -98, -16, 34, -99, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};



    private Product product;
    private Product updated;

    @Test
    public void testGetProducts() throws Exception {
        assertTrue(productService.getProducts().contains(product));
        assertEquals(1, productService.getProducts().size());
    }

    @Test(expected = NotFoundException.class)
    public void testGetProductNotFound() throws Exception {
        assertEquals(product, productService.getProduct(2));
    }

    @Test
    public void testGetProduct() throws Exception {
        assertEquals(product, productService.getProduct(1));
    }

    @Test
    public void testUpdate() throws Exception {
        Product newProduct = productService.update(1, updated);
        assertEquals("Pepsi", newProduct.getName());
        assertEquals("/products/1/image.jpg", newProduct.getImageUrl());
        assertEquals(new BigDecimal(10), newProduct.getPrice());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void testUpdateImage() throws Exception {
        productService.updateCoverImage(imageJpg, 1);
        assertTrue(product.getImageUrl().endsWith("1/image.jpeg"));

        productService.updateCoverImage(imagePng, 1);
        assertTrue(product.getImageUrl().endsWith("1/image.png"));

        assertEquals(product.getImageData(), imageData);

        verify(productRepository, times(2)).save(product);
    }

    @Test
    public void addTest() {
        productService.add(updated);

        verify(productRepository, times(1)).save(updated);
    }

    @Test
    public void deleteTest() {
        assertEquals(product, productService.delete(1));

        verify(productRepository, times(1)).delete(1);
    }

    @Test
    public void getProductsByCategory() throws Exception {
        String categoryName = "Drink";
        List<Product> drinks = productService.getProductsByCategory(categoryName);

        assertNotNull(drinks);
        assertEquals(categoryName, drinks.get(0).getCategory().getName());
    }

    @Before
    public void setUp() throws Exception {
        productService = new ProductService(productRepository, favoritesRepository, imageRepository);

        product = new Product();
        product.setId(1);
        product.setName("Cola");
        product.setAddedTime(Instant.now());
        product.setPrice(new BigDecimal(10));
        product.setImageUrl("/products/1/image.jpg");
        product.setImageUrls("urls");
        product.setCategory(new Category(1L, "Drink"));

        updated = new Product();
        updated.setName("Pepsi");
        updated.setPrice(new BigDecimal(10));
        updated.setAddedTime(Instant.now());
        updated.setCategory(new Category(1L, "Drink"));
        updated.setImageUrl("/products/1/image.jpg");


        when(productRepository.findOne(1)).thenReturn(product);
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(productRepository.getProductByCategory_Name(anyString())).thenReturn(Collections.singletonList(product));

        when(imagePng.getContentType()).thenReturn("image/png");
        when(imageJpg.getContentType()).thenReturn("image/jpeg");

        when(imagePng.getBytes()).thenReturn(imageData);
        when(imagePng.getBytes()).thenReturn(imageData);
        when(imagePng.getInputStream()).thenReturn(new ByteArrayInputStream(realImage));
        when(imageJpg.getInputStream()).thenReturn(new ByteArrayInputStream(realImage));
    }
}