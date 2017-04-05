package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductImageRepository;
import org.apache.commons.io.FileUtils;
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

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        this.imageService.addImage(testFile, productId);
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
        // clean products folder
        String folder = "products";
        Path path = Paths.get(String.format("%s/%s", imageStoragePath, folder));
        FileUtils.deleteDirectory(path.toFile());
    }

}