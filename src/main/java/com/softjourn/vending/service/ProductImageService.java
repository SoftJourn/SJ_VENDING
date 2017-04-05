package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductImageRepository;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.exceptions.NotImageException;
import com.softjourn.vending.exceptions.WrongImageDimensions;
import com.softjourn.vending.utils.FileUploadUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_HEIGHT;
import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_WIDTH;

@Service
public class ProductImageService {

    private final static String PRODUCTS_RELATIVE_ENDPOINT = "products";
    private final static String IMAGES_ENDPOINT = "images";

    private final ProductImageRepository repository;
    private String imageStoragePath;

    @Autowired
    public ProductImageService(ProductImageRepository repository,
                               @Value("${image.storage.path}") String imageStoragePath) {
        this.repository = repository;
        this.imageStoragePath = imageStoragePath;
    }

    void addImage(MultipartFile file, int productId) throws IOException {
        saveImageToDb(file, productId);
        storeToFileSystem(file, productId);
    }

    private void storeToFileSystem(MultipartFile file, int productId) throws FileAlreadyExistsException {
        String url = String.format("%s/%s/%s/%s",imageStoragePath,
            PRODUCTS_RELATIVE_ENDPOINT, productId, file.getOriginalFilename());
        Path path = Paths.get(url);
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            Files.write(path,file.getBytes());
        } catch (FileAlreadyExistsException e){
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Can not create file with "+url +" path", e);
        }
    }

    private ProductImage saveImageToDb(@NonNull MultipartFile file, Integer productId) throws IOException {
        return this.saveImageToDb(file, productId, false);
    }

    private ProductImage saveImageToDb(@NonNull MultipartFile file, Integer productId, boolean isCover) throws IOException {
        validateImage(file);
        String resolution = FileUploadUtil.getResolution(file);
        if (isCover)
            this.dropCover(productId);
        ProductImage image = new ProductImage(file.getBytes(), productId, resolution);
        image.setCover(isCover);
        ProductImage stored = repository.save(image);
        return repository.save(this.setUrlTo(stored));
    }

    private void dropCover(Integer productId) {
        this.repository.findByProductIdAndIsCover(productId, true)
            .stream()
            .peek(image -> image.setCover(false))
            .forEach(this.repository::save);
    }

    private void validateImage(@NonNull MultipartFile file) throws IOException {
        this.validateImageMimeType(file);
        this.validateImageDimensions(ImageIO.read(file.getInputStream()));
    }


    private void validateImageMimeType(MultipartFile file) {
        String supportedTypes = "image/(?:jpeg|png|jpg|apng|svg|bmp)";
        if (!file.getContentType().matches(supportedTypes)) {
            throw new NotImageException("File is not image");
        }
    }

    private void validateImageDimensions(BufferedImage image) {
        if (image.getWidth() > IMAGE_DIMENSIONS_MAX_WIDTH || image.getHeight() > IMAGE_DIMENSIONS_MAX_HEIGHT) {
            throw new WrongImageDimensions("Wrong image dimensions");
        }
    }

    private ProductImage setUrlTo(ProductImage image) {
        if (image.getId() == null || image.getProductId() == null || image.getResolution() == null) {
            throw new IllegalArgumentException("Can't form urls due to image or product id is not set");
        } else {
            int productId = image.getProductId();
            long imageId = image.getId();
            String type = image.getResolution();
            String url = PRODUCTS_RELATIVE_ENDPOINT + '/' + productId + '/' + IMAGES_ENDPOINT + '/'
                + imageId + '.' + type;
            image.setUrl(url);
            return image;
        }
    }
}
