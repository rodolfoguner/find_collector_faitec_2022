package br.fai.findcollectors.dto.response;

import br.fai.findcollectors.mapper.CollectMapper;
import br.fai.findcollectors.mapper.PersonMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import static br.fai.findcollectors.ApiTestFixtures.collect;
import static br.fai.findcollectors.ApiTestFixtures.recycler;
import static org.assertj.core.api.Assertions.assertThat;

class ResponseSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void shouldSerializePersonPublicContractAndSummaryOnly() throws Exception {
        JsonNode json = serialize(PersonMapper.toResponse(recycler()));

        assertThat(fieldNames(json)).containsExactlyInAnyOrder(
                "id", "email", "name", "telephone", "personType", "godfather",
                "garbageType", "description", "address"
        );
        assertThat(json.path("personType").asText()).isEqualTo("RECYCLER");
        assertThat(json.path("garbageType")).extracting(JsonNode::asText)
                .containsExactly("PAPER", "GLASS");
        assertThat(fieldNames(json.path("godfather")))
                .containsExactlyInAnyOrder("id", "name", "personType");
        assertThat(json.toString())
                .doesNotContain("password", "createdAt", "updatedAt", "private-godfather@example.com");
    }

    @Test
    void shouldSerializeCollectEnumsDatesAuditAndParticipantSummaries() throws Exception {
        JsonNode json = serialize(CollectMapper.toResponse(collect()));

        assertThat(json.path("status").asText()).isEqualTo("ACCEPTED");
        assertThat(json.path("garbageType")).extracting(JsonNode::asText)
                .containsExactly("PAPER", "METAL");
        assertThat(json.path("dateAndTime").asText()).isEqualTo("2026-10-07T14:30:00");
        assertThat(json.path("createdAt").asText()).isEqualTo("2026-10-01T09:15:00");
        assertThat(json.path("updatedAt").asText()).isEqualTo("2026-10-02T10:45:00");
        assertThat(fieldNames(json.path("collector")))
                .containsExactlyInAnyOrder("id", "name", "personType");
        assertThat(fieldNames(json.path("recycler")))
                .containsExactlyInAnyOrder("id", "name", "personType");
        assertThat(json.toString()).doesNotContain("password", "email", "private-collector@example.com");
    }

    @Test
    void shouldSerializeNullCollectorWithoutFailure() throws Exception {
        var source = collect();
        source.setCollector(null);

        JsonNode json = serialize(CollectMapper.toResponse(source));

        assertThat(json.has("collector")).isTrue();
        assertThat(json.get("collector").isNull()).isTrue();
    }

    @Test
    void shouldSerializeAuthenticationResponseWithStableNamesAndIsoInstants() throws Exception {
        AuthResponse response = new AuthResponse(
                "Bearer",
                "access-token",
                Instant.parse("2026-10-07T17:45:00Z"),
                "refresh-token",
                Instant.parse("2026-10-14T17:30:00Z")
        );

        JsonNode json = serialize(response);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder(
                "tokenType", "accessToken", "accessTokenExpiresAt",
                "refreshToken", "refreshTokenExpiresAt"
        );
        assertThat(json.path("accessTokenExpiresAt").asText()).isEqualTo("2026-10-07T17:45:00Z");
        assertThat(json.path("refreshTokenExpiresAt").asText()).isEqualTo("2026-10-14T17:30:00Z");
    }

    @Test
    void shouldSerializeOnlyThePublicExceptionContract() throws Exception {
        ExceptionResponse response = new ExceptionResponse(
                "PERSON_NOT_FOUND",
                "Person with id 1 not found",
                "/api/person/1",
                Instant.parse("2026-10-07T17:30:00Z")
        );

        JsonNode json = serialize(response);

        assertThat(fieldNames(json)).containsExactlyInAnyOrder("code", "message", "path", "timestamp");
        assertThat(json.path("timestamp").asText()).isEqualTo("2026-10-07T17:30:00Z");
        assertThat(json.toString()).doesNotContain("exception", "stackTrace", "className");
    }

    private JsonNode serialize(Object value) throws Exception {
        return objectMapper.readTree(objectMapper.writeValueAsBytes(value));
    }

    private Set<String> fieldNames(JsonNode json) {
        Set<String> names = new LinkedHashSet<>();
        json.fieldNames().forEachRemaining(names::add);
        return names;
    }
}
