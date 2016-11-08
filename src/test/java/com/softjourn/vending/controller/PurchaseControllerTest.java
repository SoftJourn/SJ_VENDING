package com.softjourn.vending.controller;

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

import static com.softjourn.vending.controller.ControllerTestConfig.purchaseFilter;
import static com.softjourn.vending.controller.ControllerTestConfig.purchaseWrongFilter;
import static com.softjourn.vending.controller.ProductsControllerTest.json;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ControllerTestConfig.class)
@WebAppConfiguration
public class PurchaseControllerTest {

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
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void getPurchasesByFilter() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/purchases/filter?page=0&size=10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(purchaseFilter)))
                .andExpect(status().isOk())
                .andDo(document("filtered-purchases-request", preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        requestFields(
                                fieldWithPath("machineId").description("Machine id(Required field)"),
                                fieldWithPath("type").description("Filter type(Required field)"),
                                fieldWithPath("timeZoneOffSet").description("Time zone offset, should be equal - javascript 'new Date().getTimeZoneOffset()'(Required field)"),
                                fieldWithPath("start").description("Start date(Has to be set if type equal 'Start-Due', otherwise can be empty)(Required field)"),
                                fieldWithPath("due").description("Due date(Has to be set if type equal 'Start-Due', otherwise can be empty)(Required field)")
                        )))
                .andDo(document("filtered-purchases-response", preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("content").description("Get history of user purchases"),
                                fieldWithPath("content[0].account").description("User ldap"),
                                fieldWithPath("content[0].date").description("Purchase date"),
                                fieldWithPath("content[0].product").description("Purchased product"),
                                fieldWithPath("content[0].price").description("Product price"),
                                fieldWithPath("last").description("Is page last"),
                                fieldWithPath("totalPages").description("Pages quantity"),
                                fieldWithPath("totalElements").description("Elements quantity"),
                                fieldWithPath("sort").description("Sorting"),
                                fieldWithPath("first").description("Is page first"),
                                fieldWithPath("numberOfElements").description("The number of elements currently on this page"),
                                fieldWithPath("size").description("The size of the page"),
                                fieldWithPath("number").description("The number of the current page")
                        )));
    }

    @Test
    public void getPurchasesFilterValidation() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/purchases/filter?page=0&size=10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(purchaseWrongFilter)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Error")))
                .andExpect(jsonPath("$.detail", is("The start date is after the due date (or vice verse), or wrong date format(try yyyy-mm-dd)")))
                .andExpect(jsonPath("$.code").doesNotExist())
                .andExpect(jsonPath("$.developerMessage", is("org.springframework.web.bind.MethodArgumentNotValidException")))
                .andDo(document("filtered-purchases-error", preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer [ACCESS_TOKEN_VALUE]")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("code").description("Error code(Could be null)"),
                                fieldWithPath("developerMessage").description("Contain info about server exception")
                        )));
    }


}
