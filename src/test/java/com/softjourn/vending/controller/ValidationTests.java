package com.softjourn.vending.controller;

import com.softjourn.vending.dto.ErrorDetail;
import com.softjourn.vending.entity.Categories;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;

import static com.softjourn.vending.controller.ControllerTestConfig.snacks;
import static com.softjourn.vending.controller.ProductsControllerTest.json;
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
public class ValidationTests {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    private static Categories nameWithNumberCategory;
    private static Product nameWithNumberProduct;
    private static Product negativePriceProduct;
    private static Product nullCategoryProduct;

    private static ErrorDetail nameWithNumberCategoryError;
    private static ErrorDetail nameWithNumberProductError;
    private static ErrorDetail negativePriceProductError;
    private static ErrorDetail nullCategoryProductError;

    static {
        // --- Entities---
        nameWithNumberCategory = new Categories(null, "Sn8ck");
        nameWithNumberProduct = new Product(null, new BigDecimal(20), "Snickers999", "/image.jpg", new byte[10],
                Instant.ofEpochMilli(1_000_000), "Some thing", snacks);

        negativePriceProduct = new Product(null, new BigDecimal(-20), "Snickers", "/image.jpg", new byte[10],
                Instant.ofEpochMilli(1_000_000), "Some thing", snacks);

        nullCategoryProduct = new Product(null, new BigDecimal(20), "Snickers", "/image.jpg", new byte[10],
                Instant.ofEpochMilli(1_000_000), "Some thing", null);

        //---Error response objects---
        nameWithNumberCategoryError = new ErrorDetail("Error",
                "Category name should not contain numbers and starts with symbols",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

        nameWithNumberProductError = new ErrorDetail("Error",
                "Product name should not contain numbers and starts with symbols",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

        negativePriceProductError = new ErrorDetail("Error",
                "Price should be positive",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

        nullCategoryProductError = new ErrorDetail("Error",
                "Product category is required",
                null, "org.springframework.web.bind.MethodArgumentNotValidException");

    }

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
    public void postWrongCategoryName() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(nameWithNumberCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(nameWithNumberCategoryError)))
                .andDo(document("category-name-validator", preprocessResponse(prettyPrint()),
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
                        .content(json(nameWithNumberProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(nameWithNumberProductError)))
                .andDo(document("product-name-validator", preprocessResponse(prettyPrint()),
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
                .andDo(document("product-price-validator", preprocessResponse(prettyPrint()),
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
    public void postNullProductCategory() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(nullCategoryProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(nullCategoryProductError)))
                .andDo(document("product-price-validator", preprocessResponse(prettyPrint()),
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
