package com.softjourn.vending.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.softjourn.vending.entity.Product;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ControllerTestConfig.class)
@WebAppConfiguration
public class ProductsControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter writer = mapper.writer();

    static Product product;

    @Before
    public synchronized void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .snippets()
                        .withTemplateFormat(TemplateFormats.asciidoctor()))

                .build();
    }

    @Test
    public void getProducts() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders.get("/v1/products"))
                .andExpect(status().isOk())
                .andDo(document("all-products", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("All products registered in system."),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image."),
                                fieldWithPath("[0].category").description("Product category."),
                                fieldWithPath("[0].description").description("Product description.")
                        )));
    }

    @Test
    public void getProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders.get("/v1/products/{productId}", 0))
                .andExpect(status().isOk())
                .andDo(document("all-product", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("category").description("Product category."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }



    @Test
    @WithMockUser(roles="ADMIN")
    public void addProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(product)))
                .andExpect(status().isOk())
                .andExpect(content().json(json(product)))
                .andDo(document("add-product", preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("category").description("Product category."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void updateProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products/{productId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(product)))
                .andExpect(status().isOk())
                .andExpect(content().json(json(product)))
                .andDo(document("update-product", preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("category").description("Product category."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void updateImage() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .fileUpload("/v1/products/{productId}/image", 0).file("file", "imageFileContent".getBytes())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("update-product-image", preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        )));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void deleteProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .delete("/v1/products/{productId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andExpect(content().json(json(product)))
                .andDo(document("delete-product", preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("category").description("Product category."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }


    public static String json(Object o) throws IOException {
        return writer.writeValueAsString(o);
    }
}