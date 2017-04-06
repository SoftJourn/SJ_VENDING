package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductImageRepository;
import com.softjourn.vending.entity.ProductImage;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class ProductImageServiceTest {

    private final String testImageName = "test_image.png";

    private ProductImageService imageService;

    @Autowired
    private ProductImageRepository imageRepository;

    @Value("${image.storage.path}")
    private String imageStoragePath;

    private MockMultipartFile testFile;
    private int productTestId = 0;

    @Test
    public void add() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(image.getUrl()));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void add_Duplicate_Exception() throws Exception {
        this.imageService.add(testFile, productTestId);
        this.imageService.add(testFile, productTestId);
    }

    @Test
    public void get() throws Exception {
        byte[] image = this.imageService.get(testImageName);
        assertNotNull(image);
        assertArrayEquals(testFile.getBytes(), image);
    }

    @Test(expected = NoSuchFileException.class)
    public void get_UnrealPath_Exception() throws Exception {
        this.imageService.get("UnrealPath");
    }

    @Test
    public void addAndRead() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        byte[] imageData = this.imageService.get(image.getUrl());
        assertArrayEquals(testFile.getBytes(),imageData);
        assertArrayEquals(testFile.getBytes(),image.getData());
    }

    @Test
    public void addAndDelete() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(image.getUrl()));
        this.imageService.delete(image.getUrl());
        assertFalse(this.fileExists(image.getUrl()));
    }

    @Before
    public void setUp() throws Exception {
        // set up image service
        imageService = new ProductImageService(imageRepository, imageStoragePath);
        // mock multipart file
        String passedParameterName = "file";
        String contentType = "image/png";
        String testImagePath = "images/".concat(testImageName);
        Resource resource = new ClassPathResource(testImagePath);
        testFile = new MockMultipartFile(passedParameterName, resource.getFilename(), contentType,
            resource.getInputStream());
        // clean PRODUCTS_FOLDER folder
        cleanProductsFolder();
    }

    @After
    public void tearDown() throws Exception {
        cleanProductsFolder();
    }

    private void cleanProductsFolder() throws IOException {
        String productsFolder = "products";
        Path path = Paths.get(String.format("%s/%s", imageStoragePath, productsFolder));
        FileUtils.deleteDirectory(path.toFile());
    }

    private boolean fileExists(String uri) {
        String fullPath = imageStoragePath + uri;
        File file = new File(fullPath);
        return file.exists();
    }

}