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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(ControllerTestConfig.class)
@WebMvcTest(FavoritesController.class)
@AutoConfigureMockMvc(addFilters=false)
@AutoConfigureRestDocs("target/generated-snippets")
public class FavoritesControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private String json(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }

    @Test
    @WithMockUser
    public void get() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/favorites")
                        .principal(() -> "user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("favorites", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("All user's favorites products."),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image."),
                                fieldWithPath("[0].imageUrls").description("Relative path to all product images."),
                                fieldWithPath("[0].category.id").description("Category id."),
                                fieldWithPath("[0].category.name").description("Category name."),
                                fieldWithPath("[0].nutritionFacts").description("Product nutrition facts."),
                                fieldWithPath("[0].description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser
    public void add() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/favorites/{productId}", 0)
                        .principal(() -> "user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("add-favorite", preprocessResponse(prettyPrint())));
    }

    @Test
    @WithMockUser
    public void delete() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .delete("/v1/favorites/{productId}", 0)
                        .principal(() -> "user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("delete-favorite", preprocessResponse(prettyPrint())));
    }

}
