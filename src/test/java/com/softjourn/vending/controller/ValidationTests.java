package com.softjourn.vending.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.vending.dto.ErrorDetail;
import com.softjourn.vending.entity.Category;
import com.softjourn.vending.entity.Product;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import static com.softjourn.vending.controller.ControllerTestConfig.snacks;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Import(ControllerTestConfig.class)
@WebMvcTest
@AutoConfigureMockMvc(secure = false)
@AutoConfigureRestDocs("target/generated-snippets")
public class ValidationTests {

    @Autowired
    private MockMvc mockMvc;

    private static Category nameWithNumberCategory;
    private static Product nameWithSpecialCharProduct;
    private static Product negativePriceProduct;
    private static Product nullCategoryProduct;

    private static ErrorDetail nameWithNumberCategoryError;
    private static ErrorDetail nameWithSpecialCharProductError;
    private static ErrorDetail negativePriceProductError;
    private static ErrorDetail nullCategoryProductError;

    static {
        // --- Entities---
        nameWithNumberCategory = new Category(null, "Sn8ck");
        nameWithSpecialCharProduct = new Product(null, new BigDecimal(20), "Snickers999@",
                Instant.ofEpochMilli(1_000_000), "Some thing", snacks, null, null);

        negativePriceProduct = new Product(null, new BigDecimal(-20), "Snickers",
                Instant.ofEpochMilli(1_000_000), "Some thing", snacks, null, null);

        nullCategoryProduct = new Product(null, new BigDecimal(20), "Snickers",
                Instant.ofEpochMilli(1_000_000), "Some thing", null, null, null);

        //---Error response objects---
        nameWithNumberCategoryError = new ErrorDetail("Error",
                "Category name should not contain numbers and starts with symbols",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

        nameWithSpecialCharProductError = new ErrorDetail("Error",
                "Product name should't starts and ends with whitespaces and should't contain special characters",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

        negativePriceProductError = new ErrorDetail("Error",
                "Price should be positive",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

        nullCategoryProductError = new ErrorDetail("Error",
                "Product category is required",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

    }

    @Autowired
    private ObjectMapper mapper;

    private String json(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }

    @Test
    public void postWrongCategoryName() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(nameWithNumberCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(nameWithNumberCategoryError)))
                .andDo(document("category-name-validator",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("detail").description("Error details notification"),
                                fieldWithPath("code").description("Sql code(is not required)"),
                                fieldWithPath("developerMessage").description("Error details notification for developers")
                        )));
    }


    @Test
    public void postWrongProductName() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(nameWithSpecialCharProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(nameWithSpecialCharProductError)))
                .andDo(document("product-name-validator",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("detail").description("Error details notification"),
                                fieldWithPath("code").description("Sql code(is not required)"),
                                fieldWithPath("developerMessage").description("Error details notification for developers")
                        )));
    }

    @Test
    public void postWrongProductPrice() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(negativePriceProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(negativePriceProductError)))
                .andDo(document("product-price-validator",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("detail").description("Error details notification"),
                                fieldWithPath("code").description("Sql code(is not required)"),
                                fieldWithPath("developerMessage").description("Error details notification for developers")
                        )));
    }

}
