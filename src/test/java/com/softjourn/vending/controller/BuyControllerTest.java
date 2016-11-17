package com.softjourn.vending.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.Collections;

import static com.softjourn.vending.controller.ControllerTestConfig.product;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@Import(ControllerTestConfig.class)
@WebMvcTest(BuyController.class)
@AutoConfigureMockMvc(secure = false)
@AutoConfigureRestDocs("target/generated-snippets")
public class BuyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String generateJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @WithMockUser
    public void testGetMachines() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/machines")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is(notNullValue())))
                .andDo(document("machines", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("All installed vending machines."),
                                fieldWithPath("[0]").description("Vending machine."),
                                fieldWithPath("[0].id").description("Vending machine id."),
                                fieldWithPath("[0].name").description("Vending machine name."),
                                fieldWithPath("[0].size").description("Vending machine size."),
                                fieldWithPath("[0].size.rows").description("Vending machine rows count."),
                                fieldWithPath("[0].size.columns").description("Vending machine columns count(Works correctly only for \"rectangular\" machines).")
                        )));
    }

    @Test
    @WithMockUser
    public void testGetMachine() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/machines/{machineId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("machine", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Vending machine id."),
                                fieldWithPath("name").description("Vending machine name."),
                                fieldWithPath("size").description("Vending machine size."),
                                fieldWithPath("size.rows").description("Vending machine rows count."),
                                fieldWithPath("size.columns").description("Vending machine columns count(Works correctly only for \"rectangular\" machines).")
                        )));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAvailableProducts() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/machines/{machineId}/products", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("products",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("All available now products in this vending machine."),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image.")
                        )));
    }

    @Test
    @WithMockUser
    public void getAvailableProductsByCategory() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/v1/machines/{machineId}/products/{categoryName}", 0, "Drink")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE"))
                .andExpect(status().isOk())
                .andExpect(content().json(generateJsonString(Collections.singletonList(product))))
                .andDo(document("vm-products-by-category",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("machineId").description("Machine ID"),
                                parameterWithName("categoryName").description("Category name")
                        ),
                        responseFields(
                                fieldWithPath("[]")
                                        .description("All available products in this vending machine by category."),
                                fieldWithPath("[0]").description("Product."),
                                fieldWithPath("[0].id").description("Product id."),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].imageUrl").description("Relative path to product image.")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void testGetFeatures() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/machines/{machineId}/features", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("features",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("lastAdded").description("Top 10 newest products."),
                                fieldWithPath("bestSellers").description("Top 10 best sellers products."),
                                fieldWithPath("categories").description("Products grouped by categories")
                                )));
    }

    @Test
    @WithMockUser
    public void testGetLastPurchases() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/machines/last", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("lastPurchases",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("The 10 last purchases of user"),
                                fieldWithPath("[0].name").description("Product name."),
                                fieldWithPath("[0].price").description("Product price."),
                                fieldWithPath("[0].time").description("Purchase date.")
                        )));
    }

    @Test
    @WithMockUser
    public void testBuyById() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/machines/{machineId}/fields/{fieldInternalId}", 0, "A1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ACCESS_TOKEN_VALUE"))
                .andExpect(status().isOk())
                .andDo(
                        document("buy-by-id",
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                                )));
    }

    @Test
    @WithMockUser
    public void testBuyByProduct() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/machines/{machineId}/products/{productId}", 0, 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(
                        document("buy-by-product",
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                                )));
    }
}