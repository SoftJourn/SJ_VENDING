package com.softjourn.vending.controller;

import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
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

import static com.softjourn.vending.controller.ProductsControllerTest.json;
import static com.softjourn.vending.dto.VendingMachineBuilderDTO.Numbering.ALPHABETICAL;
import static com.softjourn.vending.dto.VendingMachineBuilderDTO.Numbering.NUMERICAL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
public class VendingControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private VendingMachineBuilderDTO vendingMachineBuilder;

    static Field field;
    static Row row;

    @Before
    public synchronized void setUp() {

        vendingMachineBuilder = new VendingMachineBuilderDTO();
        vendingMachineBuilder.setName("Machine");
        vendingMachineBuilder.setRowsCount(2);
        vendingMachineBuilder.setRowsNumbering(ALPHABETICAL);
        vendingMachineBuilder.setColumnsCount(2);
        vendingMachineBuilder.setColumnsNumbering(NUMERICAL);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .snippets()
                        .withTemplateFormat(TemplateFormats.asciidoctor()))

                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending")
                        .content(json(vendingMachineBuilder))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", is(notNullValue())))
                .andDo(document("add-machine", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Vending machine id."),
                                fieldWithPath("name").description("Vending machine name."),
                                fieldWithPath("size").description("Vending machine size."),
                                fieldWithPath("size.rows").description("Vending machine rows count."),
                                fieldWithPath("size.columns").description("Vending machine columns count(Works correctly only for \"rectangular\" machines)."),
                                fieldWithPath("rows").description("Array of vending machine rows of fields."),
                                fieldWithPath("rows[0]").description("Vending machine row."),
                                fieldWithPath("rows[0].fields").description("Array of fields in row."),
                                fieldWithPath("rows[0].fields[0]").description("Vending machine field."),
                                fieldWithPath("rows[0].fields[0].id").description("Vending machine field id."),
                                fieldWithPath("rows[0].fields[0].internalId").description("Vending machine field internal id in machine."),
                                fieldWithPath("rows[0].fields[0].product").description("Product that this field contains."),
                                fieldWithPath("rows[0].fields[0].count").description("Count of products that is contained in this field.")
                        )));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .delete("/v1/vending/{machineId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("delete-machine", preprocessResponse(prettyPrint())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateField() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending/{machineId}/fields/{fieldId}", 0, 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(field))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("update-machine-field", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Field id."),
                                fieldWithPath("internalId").description("Field id in machine(usually contains row and column number)."),
                                fieldWithPath("count").description("Count of products that is in this field."),
                                fieldWithPath("product").description("Product that is in this field.")
                        )));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateRow() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending/{machineId}/rows/{rowId}", 0, 0)
                        .content("5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("update-machine-row", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Row id in db."),
                                fieldWithPath("rowId").description("Row id in machine(usually ALPHABETICAL character)."),
                                fieldWithPath("fields").description("Fields in this row.")
                        )));
    }

}