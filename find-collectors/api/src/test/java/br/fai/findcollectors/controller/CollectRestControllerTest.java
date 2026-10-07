package br.fai.findcollectors.controller;

import br.fai.findcollectors.config.GlobalExceptionHandler;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.usecases.collect.CollectQueryUseCase;
import br.fai.findcollectors.usecases.collect.CreateCollectUseCase;
import br.fai.findcollectors.usecases.collect.DeleteCollectUseCase;
import br.fai.findcollectors.usecases.collect.UpdateCollectUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CollectRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CollectRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CollectQueryUseCase queryUseCase;
    @MockitoBean
    private CreateCollectUseCase createCollectUseCase;
    @MockitoBean
    private UpdateCollectUseCase updateCollectUseCase;
    @MockitoBean
    private DeleteCollectUseCase deleteCollectUseCase;
    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void createReturns201AndMapsAllContractFields() throws Exception {
        Collect created = ControllerTestFixtures.collect(21L);
        created.setCollector(ControllerTestFixtures.collector(12L));
        created.setStatus(br.fai.findcollectors.enums.CollectStatus.ACCEPTED);
        when(createCollectUseCase.execute(any())).thenReturn(created);

        mockMvc.perform(post("/api/collect").contentType(MediaType.APPLICATION_JSON).content(validCreate()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/collect/21"))
                .andExpect(jsonPath("$.id").value(21))
                .andExpect(jsonPath("$.dateAndTime").value("2026-10-20T14:30:00"))
                .andExpect(jsonPath("$.garbageType[0]").value("PAPER"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.recurrent").value(true))
                .andExpect(jsonPath("$.address.city").value("Itajuba"))
                .andExpect(jsonPath("$.recycler.id").value(11))
                .andExpect(jsonPath("$.collector.id").value(12));

        ArgumentCaptor<Collect> captor = ArgumentCaptor.forClass(Collect.class);
        verify(createCollectUseCase).execute(captor.capture());
        Collect mapped = captor.getValue();
        assertThat(mapped.getDateAndTime()).isEqualTo(LocalDateTime.of(2026, 10, 20, 14, 30));
        assertThat(mapped.getGarbageType()).containsExactly(GarbageType.PAPER, GarbageType.GLASS);
        assertThat(mapped.isRecurrent()).isTrue();
        assertThat(mapped.getAddress().getCity()).isEqualTo("Itajuba");
        assertThat(mapped.getRecycler().getId()).isEqualTo(11L);
    }

    @Test
    void createReturnsRecyclerNotFound() throws Exception {
        when(createCollectUseCase.execute(any())).thenThrow(new NotFoundException(
                ErrorCode.RECYCLER_NOT_FOUND, "Recycler with id 11 not found"));
        assertError(post("/api/collect").contentType(MediaType.APPLICATION_JSON).content(validCreate()),
                404, "RECYCLER_NOT_FOUND", "/api/collect");
    }

    @Test
    void createReturnsBusinessRuleViolationForNonRecycler() throws Exception {
        when(createCollectUseCase.execute(any())).thenThrow(new BusinessRuleException(
                "Person must be a recycler"));
        assertError(post("/api/collect").contentType(MediaType.APPLICATION_JSON).content(validCreate()),
                422, "BUSINESS_RULE_VIOLATION", "/api/collect");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[null],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"BATTERY\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":11}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"}}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"},\"recyclerId\":0}"
    })
    void createRejectsInvalidPayloadWithoutCallingUseCase(String body) throws Exception {
        assertError(post("/api/collect").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/collect");
        verify(createCollectUseCase, never()).execute(any());
    }

    @Test
    void findAllMapsListIncludingNullCollectorAndTimestamps() throws Exception {
        when(queryUseCase.find()).thenReturn(List.of(ControllerTestFixtures.collect(21L)));
        mockMvc.perform(get("/api/collect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(21))
                .andExpect(jsonPath("$[0].collector").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$[0].recycler.name").value("Recycler"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].createdAt").value("2026-10-01T09:00:00"))
                .andExpect(jsonPath("$[0].updatedAt").value("2026-10-02T10:00:00"))
                .andExpect(jsonPath("$[0].recycler.password").doesNotExist());
    }

    @Test
    void findAllReturnsEmptyArray() throws Exception {
        when(queryUseCase.find()).thenReturn(List.of());
        mockMvc.perform(get("/api/collect"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findByIdReturnsMappedCollect() throws Exception {
        when(queryUseCase.findById(21L)).thenReturn(ControllerTestFixtures.collect(21L));
        mockMvc.perform(get("/api/collect/21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(21));
        verify(queryUseCase).findById(21L);
    }

    @Test
    void findByIdReturnsNotFound() throws Exception {
        when(queryUseCase.findById(99L)).thenThrow(collectNotFound(99L));
        assertError(get("/api/collect/99"), 404, "COLLECT_NOT_FOUND", "/api/collect/99");
    }

    @Test
    void findByIdRejectsInvalidIdFormat() throws Exception {
        assertError(get("/api/collect/nope"), 400, "VALIDATION_ERROR", "/api/collect/nope");
    }

    @Test
    void updateMapsOnlyAllowedFieldsAndPassesPathId() throws Exception {
        when(updateCollectUseCase.execute(eq(21L), any())).thenReturn(ControllerTestFixtures.collect(21L));
        mockMvc.perform(put("/api/collect/21").contentType(MediaType.APPLICATION_JSON).content("""
                        {"dateAndTime":"2026-10-20T14:30:00","garbageType":["PAPER","GLASS"],
                         "recurrent":true,"address":%s,"status":"COMPLETED",
                         "recycler":{"id":999},"collector":{"id":998}}
                        """.formatted(ControllerTestFixtures.ADDRESS_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(21));

        ArgumentCaptor<Collect> captor = ArgumentCaptor.forClass(Collect.class);
        verify(updateCollectUseCase).execute(eq(21L), captor.capture());
        Collect mapped = captor.getValue();
        assertThat(mapped.getStatus()).isNull();
        assertThat(mapped.getRecycler()).isNull();
        assertThat(mapped.getCollector()).isNull();
        assertThat(mapped.getGarbageType()).containsExactly(GarbageType.PAPER, GarbageType.GLASS);
        assertThat(mapped.getAddress().getCep()).isEqualTo("37500-000");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"}}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"}}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"BATTERY\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"37500-000\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"}}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"PAPER\"],\"recurrent\":true}",
            "{\"dateAndTime\":\"2026-10-20T14:30:00\",\"garbageType\":[\"PAPER\"],\"recurrent\":true,\"address\":{" +
                    "\"cep\":\"\",\"street\":\"Street\",\"district\":\"Center\",\"city\":\"City\",\"state\":\"MG\"}}"
    })
    void updateRejectsInvalidPayloadWithoutCallingUseCase(String body) throws Exception {
        assertError(put("/api/collect/21").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/collect/21");
        verify(updateCollectUseCase, never()).execute(any(), any());
    }

    @Test
    void updateReturnsNotFound() throws Exception {
        when(updateCollectUseCase.execute(eq(99L), any())).thenThrow(collectNotFound(99L));
        assertError(put("/api/collect/99").contentType(MediaType.APPLICATION_JSON).content(validUpdate()),
                404, "COLLECT_NOT_FOUND", "/api/collect/99");
    }

    @Test
    void deleteReturnsNoContentAndPassesId() throws Exception {
        mockMvc.perform(delete("/api/collect/21"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(deleteCollectUseCase).execute(21L);
    }

    @Test
    void deleteReturnsNotFound() throws Exception {
        org.mockito.Mockito.doThrow(collectNotFound(99L)).when(deleteCollectUseCase).execute(99L);
        assertError(delete("/api/collect/99"), 404, "COLLECT_NOT_FOUND", "/api/collect/99");
    }

    @Test
    void freeCollectsUsesCorrectQueryForFilledAndEmptyLists() throws Exception {
        when(queryUseCase.findPendingCollects(11L))
                .thenReturn(List.of(ControllerTestFixtures.collect(21L)), List.of());
        assertSpecificQuery("/api/collect/free-collects/11");
        verify(queryUseCase, org.mockito.Mockito.times(2)).findPendingCollects(11L);
    }

    @Test
    void myCollectsUsesCorrectQueryForFilledAndEmptyLists() throws Exception {
        when(queryUseCase.findMyCollects(11L))
                .thenReturn(List.of(ControllerTestFixtures.collect(21L)), List.of());
        assertSpecificQuery("/api/collect/my-collects/11");
        verify(queryUseCase, org.mockito.Mockito.times(2)).findMyCollects(11L);
    }

    @Test
    void acceptedCollectsUsesCorrectQueryForFilledAndEmptyLists() throws Exception {
        when(queryUseCase.findCollectorAcceptedCollects(11L))
                .thenReturn(List.of(ControllerTestFixtures.collect(21L)), List.of());
        assertSpecificQuery("/api/collect/accepted-collects/11");
        verify(queryUseCase, org.mockito.Mockito.times(2)).findCollectorAcceptedCollects(11L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"free-collects", "my-collects", "accepted-collects"})
    void specificQueriesRejectInvalidIdFormat(String endpoint) throws Exception {
        assertError(get("/api/collect/" + endpoint + "/nope"), 400, "VALIDATION_ERROR",
                "/api/collect/" + endpoint + "/nope");
    }

    private void assertSpecificQuery(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(21))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    private String validCreate() {
        return """
                {"dateAndTime":"2026-10-20T14:30:00","garbageType":["PAPER","GLASS"],
                 "recurrent":true,"address":%s,"recyclerId":11}
                """.formatted(ControllerTestFixtures.ADDRESS_JSON);
    }

    private String validUpdate() {
        return """
                {"dateAndTime":"2026-10-20T14:30:00","garbageType":["PAPER"],
                 "recurrent":false,"address":%s}
                """.formatted(ControllerTestFixtures.ADDRESS_JSON);
    }

    private NotFoundException collectNotFound(long id) {
        return new NotFoundException(ErrorCode.COLLECT_NOT_FOUND, "Collect with id " + id + " not found");
    }

    private void assertError(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            int expectedStatus,
            String code,
            String path
    ) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.timestamp", matchesPattern(
                        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z$")))
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }
}
