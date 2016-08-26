package com.softjourn.vending.utils;


import com.softjourn.vending.exceptions.NotImageException;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileUploadUtil {

    public static boolean isImage(@NonNull MultipartFile file) {
        return file.getContentType().startsWith("image/");
    }

    public static String getResolution(@NonNull MultipartFile file) {
        return file.getContentType().replace("image/", "");
    }

    public static String saveImage(MultipartFile file, String path, String name, String previousFileName) {
        if (!isImage(file)) throw new NotImageException();
        deletePrevious(path, previousFileName);
        return saveNewImage(file, path, name);
    }

    private static void deletePrevious(@NonNull String path, String previousFileName) {
        if(previousFileName == null) return;
        Path dirPath = Paths.get(path);
        Path previousImagePath = Paths.get(previousFileName).getFileName();
        Path fullPath = dirPath.resolve(previousImagePath);
        try {
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't delete previous image.");
        }
    }

    private static String saveNewImage(MultipartFile file, String path, String name) {
        String fileName;
        try {
            fileName = name + "." + getResolution(file);
            Path imagePath = Paths.get(path + File.separator + fileName);
            while (Files.exists(imagePath)) {
                fileName = name + getRandomString() + "." + getResolution(file);
                imagePath = Paths.get(path + fileName);
            }
            FileUtils.writeByteArrayToFile(imagePath.toFile(), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Can't save uploaded file.", e);
        }

        return fileName;
    }

    private static String getRandomString() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000000));
    }
}
