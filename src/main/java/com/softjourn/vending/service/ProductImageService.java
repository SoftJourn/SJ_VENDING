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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

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

    ProductImage addImage(MultipartFile file, int productId) throws IOException {
        storeToFileSystem(file, productId);
        return saveImageToDb(file, productId);
    }

    byte[] getImage(String uri) throws IOException {
        String url = this.formUrl(uri);
        // Read file
        Path path = Paths.get(url);
        try {
            return Files.readAllBytes(path);
        } catch (NoSuchFileException e){
            String message = "File with relative path ".concat(uri).concat(" doesn't exist");
            throw new NoSuchFileException(message);
        } catch (IOException e) {
            String message = "Can't read file with relative path ".concat(uri);
            throw new IOException(message, e);
        }
    }

    private void storeToFileSystem(MultipartFile file, int productId) throws FileAlreadyExistsException {
        String url = formUrl(file, productId);
        Path path = Paths.get(url);
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            Files.write(path, file.getBytes());
        } catch (FileAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Can not create file with " + url + " path", e);
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
        String url = formUri(file, productId);
        image.setUrl(url);
        return repository.save(image);
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

    private String formUrl(MultipartFile file, int productId) {
        return this.formUrl(file.getOriginalFilename(), productId);
    }

    private String formUrl(String fileName, int productId) {
        String uri = formUri(fileName, productId);
        return formUrl(uri);
    }

    private String formUrl(String uri) {
        uri = appendSlashIfNotExists(uri);
        return imageStoragePath.concat(uri);
    }

    private String formUri(MultipartFile file, int productId) {
        return this.formUri(file.getOriginalFilename(), productId);
    }

    private String formUri(String fileName, int productId) {
        return String.format("/%s/%s/%s/%s",
            PRODUCTS_RELATIVE_ENDPOINT, productId, IMAGES_ENDPOINT, fileName);
    }

    private String appendSlashIfNotExists(String uri) {
        String regex = "^/.*";
        String result = uri;
        if (!uri.matches(regex))
            result = "/".concat(uri);
        return result;
    }
}
