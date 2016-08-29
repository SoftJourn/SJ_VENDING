package com.softjourn.vending.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ControllerTestConfig.class)
@WebAppConfiguration
public class FavoritesControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter writer = mapper.writer();

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .snippets()
                        .withTemplateFormat(TemplateFormats.asciidoctor()))

                .build();
    }

    @Test
    public void get() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .get("/v1/favorites")
                        .principal(() -> "user")
                        .header("Authentication", "Bearer ACCESS_TOKEN_VALUE"))
                .andExpect(status().isOk())
                .andDo(document("favorites", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("All user's favorites products.")
                        )));
    }

    @Test
    public void add() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/favorites/{productId}", 0)
                        .principal(() -> "user")
                        .header("Authentication", "Bearer ACCESS_TOKEN_VALUE"))
                .andExpect(status().isOk())
                .andDo(document("add-favorite", preprocessResponse(prettyPrint())));
    }

    @Test
    public void delete() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .delete("/v1/favorites/{productId}", 0)
                        .principal(() -> "user")
                        .header("Authentication", "Bearer ACCESS_TOKEN_VALUE"))
                .andExpect(status().isOk())
                .andDo(document("delete-favorite", preprocessResponse(prettyPrint())));
    }

    public static String json(Object o) throws IOException {
        return writer.writeValueAsString(o);
    }

}