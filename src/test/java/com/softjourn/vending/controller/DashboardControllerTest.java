package com.softjourn.vending.controller;

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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(ControllerTestConfig.class)
@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(secure = false)
@AutoConfigureRestDocs("target/generated-snippets")
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"INVENTORY", "SUPER_ADMIN"})
    public void testGetDashboard() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/dashboard")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("dashboard", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("products").description("Products quantity."),
                                fieldWithPath("machines").description("Machines quantity."),
                                fieldWithPath("categories").description("Categories quantity."),
                                fieldWithPath("purchases").description("Purchases quantity.")
                        )));
    }


}
