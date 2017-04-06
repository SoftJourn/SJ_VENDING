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
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class ProductImageServiceTest {

    private ProductImageService imageService;

    @Autowired
    private ProductImageRepository imageRepository;

    @Value("${image.storage.path}")
    String imageStoragePath;

    private MockMultipartFile testFile;

    @Test
    public void addImage() throws Exception {
        int productId = 0;
        ProductImage image = this.imageService.addImage(testFile, productId);
        assertTrue(this.fileExists(image.getUrl()));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void addImage_Duplicate_Exception() throws Exception {
        int productId = 0;
        this.imageService.addImage(testFile, productId);
        this.imageService.addImage(testFile, productId);
    }

    @Before
    public void setUp() throws Exception {
        // set up image service
        imageService = new ProductImageService(imageRepository, imageStoragePath);
        // mock multipart file
        String passedParameterName = "file";
        String imagePath = "images/test_image.png";
        String contentType = "image/png";
        Resource resource = new ClassPathResource(imagePath);
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