package com.softjourn.vending.controller;

import com.softjourn.vending.TestHelper;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.exceptions.NoContentException;
import com.softjourn.vending.service.ProductImageService;
import com.softjourn.vending.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductsController.class)
@AutoConfigureMockMvc(secure = false)
@AutoConfigureRestDocs("target/generated-snippets")
public class ProductsControllerImageTest {

    private final int testProductId = 0;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductImageService imageService;
    private MockMultipartFile[] files;
    private List<ProductImage> images;

    public ProductsControllerImageTest() throws Exception {
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void addProductImage() throws Exception {
        String uri = "/v1/products/{id}/images";
        String authorizationHeader = "Bearer [ACCESS_TOKEN_VALUE]";
        // TODO Mock service to get response
        MockHttpServletResponse response = mockMvc.perform(RestDocumentationRequestBuilders
            .fileUpload(uri, testProductId)
            .file(files[0])
            .file(files[1])
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
            .andExpect(status().isOk())
            .andDo(document("add-image"))
            .andExpect(content().json(String.valueOf(images)))
            .andReturn().getResponse();
        System.out.println(response.getContentAsString());
    }

    @Test
    @WithMockUser
    public void getProductImage() throws Exception {
        String uri = "/v1/products/{productId}/images/{imageName}";
        String authorizationHeader = "Bearer [ACCESS_TOKEN_VALUE]";
        MultipartFile file = mockMultipartFiles()[0];
        mockMvc.perform(RestDocumentationRequestBuilders
            .get(uri, testProductId, file.getOriginalFilename())
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        )
            .andExpect(status().isOk())
            .andExpect(content().bytes(file.getBytes()));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void deleteImage() throws Exception {
        String uri = "/v1/products/{productId}/images/{imageName}";
        String authorizationHeader = "Bearer [ACCESS_TOKEN_VALUE]";
        MultipartFile file = mockMultipartFiles()[1];
        mockMvc.perform(RestDocumentationRequestBuilders
            .delete(uri, testProductId, file.getOriginalFilename())
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        )
            .andExpect(status().isNoContent());
    }

    @Before
    public void setUp() throws Exception {
        // mock multipart files
        files = this.mockMultipartFiles();
        images = this.formImages(files);
        when(this.imageService.add(files, testProductId)).thenReturn(images);
        // second file is not exists to delete
        String uri = String.format("/%s/images/%s", testProductId, files[1].getOriginalFilename());
        doThrow(new NoContentException("file already exists")).when(this.imageService).delete(uri);
    }

    private MockMultipartFile[] mockMultipartFiles() throws Exception {
        // Mock multipart files
        String testImage1Name = "test_image.png";
        String requestAttributeName = "files";
        MockMultipartFile testFile = TestHelper.mockMultipartImageFile(testImage1Name, requestAttributeName);

        String uri = String.format("/%s/images/%s", testProductId, testImage1Name);
        when(this.imageService.get(uri)).thenReturn(testFile.getBytes());

        String testImage2Name = "test_image2.png";
        MockMultipartFile testFile2 = TestHelper.mockMultipartImageFile(testImage2Name, requestAttributeName);
        return new MockMultipartFile[]{testFile, testFile2};
    }

    private List<ProductImage> formImages(MultipartFile[] files) throws IOException {
        ArrayList<ProductImage> result = new ArrayList<>();
        for (MultipartFile file : files) {
            ProductImage image = new ProductImage(file.getBytes(), testProductId, "resolution");
            image.setUrl(file.getOriginalFilename());
            result.add(image);
        }
        return result;
    }

}