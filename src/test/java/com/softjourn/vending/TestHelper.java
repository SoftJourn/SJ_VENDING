package com.softjourn.vending;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

/**
 * Common method for test processing and checking
 */
public class TestHelper {

    private static ObjectMapper mapper = new ObjectMapper();

    public static MockMultipartFile mockMultipartImageFile(String testImageName) throws IOException {
        return mockMultipartImageFile(testImageName, "file");
    }

    public static MockMultipartFile mockMultipartImageFile(String testImageName, String passedParameterName) throws IOException {
        String contentType = "image/png";
        String testImagePath;
        testImagePath = "images/".concat(testImageName);
        Resource resource = new ClassPathResource(testImagePath);
        return new MockMultipartFile(passedParameterName, resource.getFilename(), contentType,
            resource.getInputStream());
    }

    public static String json(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }
}
