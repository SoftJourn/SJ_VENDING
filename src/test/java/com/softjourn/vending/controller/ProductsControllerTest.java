package com.softjourn.vending.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static com.softjourn.vending.controller.ControllerTestConfig.product;
import static com.softjourn.vending.controller.ControllerTestConfig.product4;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(ControllerTestConfig.class)
@WebMvcTest(ProductsController.class)
@AutoConfigureMockMvc(secure = false)
@AutoConfigureRestDocs("target/generated-snippets")
public class ProductsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private String json(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void getProducts() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("all-products",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("All products registered in system."),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image."),
                                fieldWithPath("[0].category.id").description("Category id."),
                                fieldWithPath("[0].category.name").description("Category name."),
                                fieldWithPath("[0].description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void getProductsByNameThatContain() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/products/search")
                        .param("name", "Snickers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("all-products-by-name",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("Products that has being found"),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image."),
                                fieldWithPath("[0].category.id").description("Category id."),
                                fieldWithPath("[0].category.name").description("Category name."),
                                fieldWithPath("[0].description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void getProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/products/{productId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("all-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("imageUrls").description("Relative path to all product images."),
                                fieldWithPath("category.id").description("Category id."),
                                fieldWithPath("category.name").description("Category name."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void addProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(product4)))
                .andExpect(status().isOk())
                .andExpect(content().json(json(product4)))
                .andDo(document("add-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("imageUrls").description("Relative path to all product images."),
                                fieldWithPath("category.id").description("Category id."),
                                fieldWithPath("category.name").description("Category name."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void updateProduct() throws Exception {
        product.setName("Super Nuts");
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products/{productId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(product)))
                .andExpect(status().isOk())
                .andExpect(content().json(json(product)))
                .andDo(document("update-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("imageUrls").description("Relative path to all product images."),
                                fieldWithPath("category").description("Category id."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void updateImage() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .fileUpload("/v1/products/{productId}/image", 0).file("file", "imageFileContent".getBytes())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("update-product-image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void deleteProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .delete("/v1/products/{productId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andExpect(content().json(json(product)))
                .andDo(document("delete-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product id."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("price").description("Product price."),
                                fieldWithPath("imageUrl").description("Relative path to product image."),
                                fieldWithPath("imageUrls").description("Relative path to all product images."),
                                fieldWithPath("category.id").description("Category id."),
                                fieldWithPath("category.name").description("Category name."),
                                fieldWithPath("description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser
    public void getProductsByCategory() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/v1/products/category/{categoryName}", "Drink")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andExpect(content().json(json(Collections.singletonList(product))))
                .andDo(document("product-by-category",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("Products that has being found by category name"),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image."),
                                fieldWithPath("[0].imageUrls").description("Relative path to all product images."),
                                fieldWithPath("[0].category.id").description("Category id."),
                                fieldWithPath("[0].category.name").description("Category name."),
                                fieldWithPath("[0].description").description("Product description.")
                        )
                ));
    }
}