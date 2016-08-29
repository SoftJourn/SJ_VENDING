package com.softjourn.vending.controller;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ControllerTestConfig.class)
@WebAppConfiguration
public class BuyControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

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
                                fieldWithPath("[0].imageUrl").description("Relative path to product image."),
                                fieldWithPath("[0].position").description("Position object that shows where product is located in vending machine."),
                                fieldWithPath("[0].position.row").description("Number of row that contains product(Starts with 0)."),
                                fieldWithPath("[0].position.column").description("Number of column that contains product(Starts with 0)."),
                                fieldWithPath("[0].position.cellName").description("Name of cell where product is located.")
                        )));
    }

    @Test
    public void testGetFeatures() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/machines/{machineId}/features", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("features",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("New products").description("Top 10 newest products."),
                                fieldWithPath("My lastPurchases").description("Ten products that was purchased last time by user."),
                                fieldWithPath("Best sellers").description("Top 10 best sellers products."),
                                fieldWithPath("Drink").description("All drinks in machine. With positions."),
                                fieldWithPath("Snack").description("All snacks in machine. With positions.")
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