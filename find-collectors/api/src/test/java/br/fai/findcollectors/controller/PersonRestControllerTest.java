package br.fai.findcollectors.controller;

import br.fai.findcollectors.config.GlobalExceptionHandler;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.usecases.person.DeletePersonUseCase;
import br.fai.findcollectors.usecases.person.PersonQueryUseCase;
import br.fai.findcollectors.usecases.person.UpdatePersonUseCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PersonRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UpdatePersonUseCase updatePersonUseCase;
    @MockitoBean
    private DeletePersonUseCase deletePersonUseCase;
    @MockitoBean
    private PersonQueryUseCase personQueryUseCase;
    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void findAllMapsPeopleWithoutPasswordsIncludingAddressMaterialsAndGodfather() throws Exception {
        Person person = ControllerTestFixtures.recycler(11L);
        person.setGodfather(ControllerTestFixtures.collector(8L));
        when(personQueryUseCase.find()).thenReturn(List.of(person));

        mockMvc.perform(get("/api/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].garbageType[0]").value("PAPER"))
                .andExpect(jsonPath("$[0].address.city").value("Itajuba"))
                .andExpect(jsonPath("$[0].godfather.id").value(8))
                .andExpect(jsonPath("$[0].godfather.personType").value("COLLECTOR"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void findAllReturnsEmptyArray() throws Exception {
        when(personQueryUseCase.find()).thenReturn(List.of());
        mockMvc.perform(get("/api/person"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findByIdReturnsMappedPersonAndPassesId() throws Exception {
        when(personQueryUseCase.findById(11L)).thenReturn(ControllerTestFixtures.recycler(11L));
        mockMvc.perform(get("/api/person/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.personType").value("RECYCLER"));
        verify(personQueryUseCase).findById(11L);
    }

    @Test
    void findByIdReturnsStandardNotFound() throws Exception {
        when(personQueryUseCase.findById(99L)).thenThrow(personNotFound(99L));
        assertError(get("/api/person/99"), 404, "PERSON_NOT_FOUND", "/api/person/99");
    }

    @Test
    void findByIdRejectsInvalidIdFormat() throws Exception {
        assertError(get("/api/person/not-a-number"), 400, "VALIDATION_ERROR", "/api/person/not-a-number");
    }

    @Test
    void updateMapsOnlyAllowedFieldsAndPassesPathIdSeparately() throws Exception {
        when(updatePersonUseCase.execute(eq(11L), any())).thenReturn(ControllerTestFixtures.recycler(11L));

        mockMvc.perform(put("/api/person/11").contentType(MediaType.APPLICATION_JSON).content("""
                        {"id":999,"email":"attacker@example.com","password":"Attacker1",
                         "godfather":{"id":999},"name":"Updated Recycler","telephone":"35988887777",
                         "personType":"RECYCLER","collectPoint":true,
                         "garbageType":["PAPER","GLASS"],"description":"Updated point",
                         "address":%s}
                        """.formatted(ControllerTestFixtures.ADDRESS_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(updatePersonUseCase).execute(eq(11L), captor.capture());
        Person mapped = captor.getValue();
        assertThat(mapped.getId()).isNull();
        assertThat(mapped.getEmail()).isNull();
        assertThat(mapped.getPassword()).isNull();
        assertThat(mapped.getGodfather()).isNull();
        assertThat(mapped.getName()).isEqualTo("Updated Recycler");
        assertThat(mapped.getPersonType()).isEqualTo(PersonType.RECYCLER);
        assertThat(mapped.getGarbageType()).containsExactly(GarbageType.PAPER, GarbageType.GLASS);
        assertThat(mapped.getAddress().getCity()).isEqualTo("Itajuba");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"\",\"personType\":\"RECYCLER\",\"garbageType\":[\"PAPER\"]}",
            "{\"name\":\"Name\",\"telephone\":\"1234567890123456\",\"personType\":\"RECYCLER\",\"garbageType\":[\"PAPER\"]}",
            "{\"name\":\"Name\",\"personType\":\"ADMIN\",\"garbageType\":[\"PAPER\"]}",
            "{\"name\":\"Name\",\"personType\":\"RECYCLER\",\"garbageType\":[\"BATTERY\"]}",
            "{\"name\":\"Name\",\"personType\":\"RECYCLER\",\"garbageType\":null}",
            "{\"name\":\"Name\",\"personType\":\"RECYCLER\",\"garbageType\":[\"PAPER\"],\"address\":{\"cep\":\"\",\"street\":\"Street\",\"district\":\"District\",\"city\":\"City\",\"state\":\"MG\"}}"
    })
    void updateRejectsInvalidPayloadWithoutCallingUseCase(String body) throws Exception {
        assertError(put("/api/person/11").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/person/11");
        verify(updatePersonUseCase, never()).execute(any(), any());
    }

    @Test
    void updateReturnsNotFound() throws Exception {
        when(updatePersonUseCase.execute(eq(99L), any())).thenThrow(personNotFound(99L));
        assertError(put("/api/person/99").contentType(MediaType.APPLICATION_JSON).content(validUpdate()),
                404, "PERSON_NOT_FOUND", "/api/person/99");
    }

    @Test
    void deleteReturnsNoContentAndPassesId() throws Exception {
        mockMvc.perform(delete("/api/person/11"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(deletePersonUseCase).execute(11L);
    }

    @Test
    void deleteReturnsNotFound() throws Exception {
        org.mockito.Mockito.doThrow(personNotFound(99L)).when(deletePersonUseCase).execute(99L);
        assertError(delete("/api/person/99"), 404, "PERSON_NOT_FOUND", "/api/person/99");
    }

    @Test
    void deleteRejectsInvalidIdFormat() throws Exception {
        assertError(delete("/api/person/nope"), 400, "VALIDATION_ERROR", "/api/person/nope");
    }

    private String validUpdate() {
        return """
                {"name":"Updated","telephone":"35999999999","personType":"RECYCLER",
                 "collectPoint":true,"garbageType":["PAPER"],"description":"Point","address":%s}
                """.formatted(ControllerTestFixtures.ADDRESS_JSON);
    }

    private NotFoundException personNotFound(long id) {
        return new NotFoundException(ErrorCode.PERSON_NOT_FOUND, "Person with id " + id + " not found");
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
