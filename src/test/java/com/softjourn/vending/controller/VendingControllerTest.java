package com.softjourn.vending.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.vending.dto.LoadHistoryRequestDTO;
import com.softjourn.vending.dto.PageRequestImpl;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import org.junit.Before;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static com.softjourn.vending.dto.VendingMachineBuilderDTO.Numbering.ALPHABETICAL;
import static com.softjourn.vending.dto.VendingMachineBuilderDTO.Numbering.NUMERICAL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(ControllerTestConfig.class)
@WebMvcTest(VendingController.class)
@AutoConfigureMockMvc(addFilters=false)
@AutoConfigureRestDocs("target/generated-snippets")
public class VendingControllerTest {

    static Field field;
    static Row row;

    @Autowired
    private MockMvc mockMvc;
    private VendingMachineBuilderDTO vendingMachineBuilder;

    @Autowired
    private ObjectMapper mapper;

    private String json(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }

    @Before
    public synchronized void setUp() {

        vendingMachineBuilder = new VendingMachineBuilderDTO();
        vendingMachineBuilder.setName("Machine");
        vendingMachineBuilder.setRowsCount(2);
        vendingMachineBuilder.setRowsNumbering(ALPHABETICAL);
        vendingMachineBuilder.setColumnsCount(2);
        vendingMachineBuilder.setColumnsNumbering(NUMERICAL);
        vendingMachineBuilder.setCellLimit(2);
        vendingMachineBuilder.setIsActive(false);
        vendingMachineBuilder.setIsVirtual(false);
        vendingMachineBuilder.setUrl("http://url.com");
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void add() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending")
                        .content(json(vendingMachineBuilder))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", is(notNullValue())))
                .andDo(document("add-machine",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Vending machine id."),
                                fieldWithPath("name").description("Vending machine name."),
                                fieldWithPath("url").description("Vending machine url."),
                                fieldWithPath("isActive").description("Vending machine is active."),
                                fieldWithPath("isVirtual").description("Vending machine is virtual(Should work without real vending machine)."),
                                fieldWithPath("uniqueId").description("Vending machine unique Id used in Coins server."),
                                fieldWithPath("size").description("Vending machine size."),
                                fieldWithPath("size.rows").description("Vending machine rows count."),
                                fieldWithPath("size.columns").description("Vending machine columns count(Works correctly only for \"rectangular\" machines)."),
                                fieldWithPath("size.cellLimit").description("Vending machine product limitation in a cell"),
                                fieldWithPath("rows").description("Array of vending machine rows of fields."),
                                fieldWithPath("rows[0]").description("Vending machine row."),
                                fieldWithPath("rows[0].id").ignored(),
                                fieldWithPath("rows[0].rowId").description("Row id in the vending machine."),
                                fieldWithPath("rows[0].fields").description("Array of fields in row."),
                                fieldWithPath("rows[0].fields[0].position").description("Vending machine field."),
                                fieldWithPath("rows[0].fields[0].id").description("Vending machine field id."),
                                fieldWithPath("rows[0].fields[0].internalId").description("Vending machine field internal id in machine."),
                                fieldWithPath("rows[0].fields[0].count").description("Count of products that is contained in this field."),
                                fieldWithPath("rows[0].fields[0].product").description("Product that this field contains."),
                                fieldWithPath("rows[0].fields[0].product.id").description("Product id."),
                                fieldWithPath("rows[0].fields[0].product.name").description("Product name."),
                                fieldWithPath("rows[0].fields[0].product.price").description("Product price."),
                                fieldWithPath("rows[0].fields[0].product.imageUrl").description("Relative path to product image."),
                                fieldWithPath("rows[0].fields[0].product.imageUrls").description("Relative path to all product images."),
                                fieldWithPath("rows[0].fields[0].product.category.id").description("Category id."),
                                fieldWithPath("rows[0].fields[0].product.category.name").description("Category name."),
                                fieldWithPath("rows[0].fields[0].product.nutritionFacts").description("Product nutrition facts."),
                                fieldWithPath("rows[0].fields[0].product.description").description("Product description.")                                )));
    }


    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void delete() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .delete("/v1/vending/{machineId}", 0)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("delete-machine", preprocessResponse(prettyPrint())));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void updateField() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending/{machineId}/fields/{fieldId}", 0, 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(field))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("update-machine-field",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Field id."),
                                fieldWithPath("internalId").description("Field id in machine(usually contains row and column number)."),
                                fieldWithPath("position").description("Position in row."),
                                fieldWithPath("count").description("Count of products that is in this field."),
                                fieldWithPath("product").description("Product that is in this field."),
                                fieldWithPath("product.id").description("Product id."),
                                fieldWithPath("product.name").description("Product name."),
                                fieldWithPath("product.price").description("Product price."),
                                fieldWithPath("product.imageUrl").description("Relative path to product image."),
                                fieldWithPath("product.imageUrls").description("Relative path to all product images."),
                                fieldWithPath("product.category.id").description("Category id."),
                                fieldWithPath("product.category.name").description("Category name."),
                                fieldWithPath("product.nutritionFacts").description("Product nutrition facts."),
                                fieldWithPath("product.description").description("Product description.")
                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void updateRow() throws Exception {
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending/{machineId}/rows/{rowId}", 0, 0)
                        .content("5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("update-machine-row",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Row id in db."),
                                fieldWithPath("rowId").description("Row id in machine(usually ALPHABETICAL character)."),
                                fieldWithPath("fields").description("Fields in this row."),
                                fieldWithPath("fields[0]").description("Field."),
                                fieldWithPath("fields[0].id").description("Field id."),
                                fieldWithPath("fields[0].internalId").description("Field internal id."),
                                fieldWithPath("fields[0].position").description("Field position."),
                                fieldWithPath("fields[0].count").description("Fields count."),
                                fieldWithPath("fields[0].product").description("Product."),
                                fieldWithPath("fields[0].product.id").description("Product id."),
                                fieldWithPath("fields[0].product.name").description("Product name."),
                                fieldWithPath("fields[0].product.price").description("Product price."),
                                fieldWithPath("fields[0].product.imageUrl").description("Relative path to product image."),
                                fieldWithPath("fields[0].product.imageUrls").description("Relative path to all product images."),
                                fieldWithPath("fields[0].product.category.id").description("Category id."),
                                fieldWithPath("fields[0].product.category.name").description("Category name."),
                                fieldWithPath("fields[0].product.nutritionFacts").description("Product nutrition facts."),
                                fieldWithPath("fields[0].product.description").description("Product description.")                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void getLoadHistoryByMachine() throws Exception {
        LoadHistoryRequestDTO requestDTO = new LoadHistoryRequestDTO();
        requestDTO.setMachineId(1);
        requestDTO.setPageable(new PageRequestImpl(10, 0, null));
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending/loads")
                        .content(json(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("loads-request", preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("machineId")
                                        .description("Machine identifier(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("start").optional()
                                        .description("Start datetime to search(Can be null)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("due").optional()
                                        .description("Due datetime to search(Can be null)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("pageable.pageNumber")
                                        .description("Size to return(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("pageable.pageSize")
                                        .description("Page to return(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("pageable.sort")
                                        .description("Sort conditions to order(Can be null)")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.offset")
                                        .description("Offset(Can be null)")
                                        .type(JsonFieldType.NUMBER)
                        )))
                .andDo(document("loads-response",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("content[0].total").description("It is price * count"),
                                fieldWithPath("content[0].date").description("Date when field was filled"),
                                fieldWithPath("content[0].productName").description("Product name"),
                                fieldWithPath("content[0].productPrice").description("Product price"),
                                fieldWithPath("content[0].cell").description("Cell id in vending machine"),
                                fieldWithPath("content[0].count").description("Amount of products, that were put into machine"),
                                fieldWithPath("last").description("Is page last"),
                                fieldWithPath("totalPages").description("Pages quantity"),
                                fieldWithPath("totalElements").description("Elements quantity"),
                                fieldWithPath("sort").description("Sorting"),
                                fieldWithPath("sort.sorted").ignored(),
                                fieldWithPath("sort.empty").ignored(),
                                fieldWithPath("sort.unsorted").ignored(),
                                fieldWithPath("empty").ignored(),
                                fieldWithPath("first").description("Is page first"),
                                fieldWithPath("numberOfElements").description("The number of elements currently on this page"),
                                fieldWithPath("size").description("The size of the page"),
                                fieldWithPath("number").description("The number of the current page"),
                                fieldWithPath("pageable.pageNumber")
                                        .description("Size to return(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("pageable.pageSize")
                                        .description("Page to return(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("pageable.sort")
                                        .description("Sort conditions to order(Can be null)")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.offset")
                                        .description("Offset(Can be null)")
                                        .type(JsonFieldType.NUMBER)

                        )));
    }

    @Test
    @WithMockUser(roles = {"SUPER_USER", "INVENTORY"})
    public void exportLoadHistoryByMachine() throws Exception {
        LoadHistoryRequestDTO requestDTO = new LoadHistoryRequestDTO();
        requestDTO.setMachineId(1);
        requestDTO.setPageable(new PageRequestImpl(10, 0, null));
        mockMvc
                .perform(RestDocumentationRequestBuilders
                        .post("/v1/vending/loads/export")
                        .content(json(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer [ACCESS_TOKEN_VALUE]"))
                .andExpect(status().isOk())
                .andDo(document("loads-export-request", preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("machineId")
                                        .description("Machine identifier(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("start").optional()
                                        .description("Start datetime to search(Can be null)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("due").optional()
                                        .description("Due datetime to search(Can be null)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("pageable.pageNumber")
                                        .description("Size to return(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("pageable.pageSize")
                                        .description("Page to return(Required field)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("pageable.sort")
                                        .description("Sort conditions to order(Can be null)")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.offset")
                                        .description("Offset(Can be null)")
                                        .type(JsonFieldType.NUMBER)
                        )))
                .andDo(document("loads-export-response",
                        preprocessResponse(prettyPrint())));
    }

}
