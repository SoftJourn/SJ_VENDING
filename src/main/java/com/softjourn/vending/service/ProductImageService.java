package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductImageRepository;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.exceptions.NoImageException;
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
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<ProductImage> add(MultipartFile[] files, Integer productId) throws IOException {
        List<ProductImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
            images.add(this.add(file, productId));
        }
        return images;
    }

    public void delete(String uri) throws IOException {
        deleteFormDB(uri);
    }

    public void deleteFromFileSystem(String uri) throws IOException {
        String url = this.formUrl(uri);
        Path path = Paths.get(url);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IOException(canNotDeleteFileMessage(uri), e);
        }
    }

    public byte[] get(String uri) throws IOException {
        String url = this.formUrl(uri);
        // Read file
        Path path = Paths.get(url);
        try {
            try {
                return Files.readAllBytes(path);
            } catch (NoSuchFileException e) {
                throw new NoSuchFileException(fileDoesNotExistsMessage(uri));
            } catch (IOException e) {
                throw new IOException(canNotReadFileMessage(uri), e);
            }
            // TODO remove old version in next release
        } catch (Exception e) {
            try {
                return this.getOldVersion(uri);
            } catch (NullPointerException nested) {
                throw new NoSuchFileException("Can not read file " + uri);
            }
        }
    }

    ProductImage add(MultipartFile file, int productId) throws IOException {
        ProductImage image = saveImageToDb(file, productId);
        // TODO Compatibility. Remove formName in next release
        String name = formName(file, image);
        storeToFileSystem(file.getBytes(), productId, name);
        return image;
    }

    ProductImage setCover(String imageName, int productId) throws NoSuchFileException {
        String uri = formUri(imageName, productId);
        ProductImage image = this.repository.findProductImageByUrl(uri);
        Optional.ofNullable(image)
            .orElseThrow(() -> new NoSuchFileException(uri));
        this.dropCover(productId);
        image.setCover(true);
        return this.repository.saveAndFlush(image);
    }

    String formUri(String fileName, int productId) {
        return String.format("%s/%s/%s/%s",
            PRODUCTS_RELATIVE_ENDPOINT, productId, IMAGES_ENDPOINT, fileName);
    }

    String appendSlashIfNotExists(String uri) {
        String regex = "^/.*";
        String result = uri;
        if (!uri.matches(regex))
            result = "/".concat(uri);
        return result;
    }

    private void storeToFileSystem(byte[] content, int productId, String name) throws FileAlreadyExistsException {
        String url = formUrl(name, productId);
        Path path = Paths.get(url);
        String errorMessage = "Can not create file with " + formUri(name, productId) + " path";
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            Files.write(path, content);
        } catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(errorMessage);
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage, e);
        }
    }

    private String formName(MultipartFile file, ProductImage image) {
        String name = file.getOriginalFilename();
        int positionOfLastDot = name.lastIndexOf(".");
        name = image.getId().toString() + name.substring(positionOfLastDot, name.length());
        return name;
    }

    private byte[] getOldVersion(String uri) {
        String url = PRODUCTS_RELATIVE_ENDPOINT.concat(uri);
        ProductImage image = repository.findProductImageByUrl(url);
        return image.getData();
    }

    private void deleteFormDB(String uri) {
        ProductImage image = this.repository.findProductImageByUrl(uri);
        Optional.ofNullable(image).orElseThrow(() -> new NoImageException("Image does not exits in DB ".concat(uri)));
        this.repository.delete(image);
    }

    private String fileDoesNotExistsMessage(String uri) {
        return "File with relative path ".concat(uri).concat(" doesn't exist");
    }

    private String canNotReadFileMessage(String uri) {
        return "Can't read file with relative path ".concat(uri);
    }

    private String canNotDeleteFileMessage(String uri) {
        return "Can't delete file with relative path ".concat(uri);
    }

    private void storeToFileSystem(MultipartFile file, int productId) throws IOException {
        this.storeToFileSystem(file.getBytes(), productId, file.getOriginalFilename());
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
        // TODO change naming convention to file name instead of product id
        ProductImage storedImage = repository.save(image);
        String name = this.formName(file, storedImage);
        String url = formUri(name, productId);
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

    private String formUrl(String uri) {
        uri = appendSlashIfNotExists(uri);
        return imageStoragePath.concat(uri);
    }

    private String formUrl(String fileName, int productId) {
        String uri = formUri(fileName, productId);
        return formUrl(uri);
    }

    private String formUri(MultipartFile file, int productId) {
        return this.formUri(file.getOriginalFilename(), productId);
    }

    private String formUri(long imageId, int productId) {
        return this.formUri(Long.valueOf(imageId).toString(), productId);
    }
}
