package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.request.CreatePersonRequest;
import br.fai.findcollectors.dto.request.UpdatePersonRequest;
import br.fai.findcollectors.dto.response.PersonResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static br.fai.findcollectors.ApiTestFixtures.addressRequest;
import static br.fai.findcollectors.ApiTestFixtures.recycler;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonMapperTest {

    @ParameterizedTest
    @ValueSource(strings = {"recycler", "RECYCLER", "ReCyClEr"})
    void shouldMapCreateRequestAndIgnoreFieldsOutsideTheContract(String personType) {
        CreatePersonRequest request = new CreatePersonRequest(
                "recycler@example.com", "Password1", "Recycler", personType
        );

        Person person = PersonMapper.toEntity(request);

        assertThat(person.getEmail()).isEqualTo("recycler@example.com");
        assertThat(person.getPassword()).isEqualTo("Password1");
        assertThat(person.getName()).isEqualTo("Recycler");
        assertThat(person.getPersonType()).isEqualTo(PersonType.RECYCLER);
        assertThat(person.getId()).isNull();
        assertThat(person.getTelephone()).isNull();
        assertThat(person.getGodfather()).isNull();
        assertThat(person.getGarbageType()).isNull();
        assertThat(person.getAddress()).isNull();
        assertThat(person.getCreatedAt()).isNull();
        assertThat(person.getUpdatedAt()).isNull();
    }

    @Test
    void shouldMapCollectorIgnoringCase() {
        assertThat(PersonMapper.toEntity(new CreatePersonRequest(
                "collector@example.com", "Password1", "Collector", "collector"
        )).getPersonType()).isEqualTo(PersonType.COLLECTOR);
    }

    @Test
    void shouldRejectInvalidPersonTypeWhenValidationIsBypassed() {
        CreatePersonRequest request = new CreatePersonRequest(
                "person@example.com", "Password1", "Person", "ADMIN"
        );

        assertThatThrownBy(() -> PersonMapper.toEntity(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("invalid personType");
    }

    @Test
    void shouldMapUpdateRequestWithoutAcceptingImmutableOrSensitiveFields() {
        UpdatePersonRequest request = validUpdateRequest(List.of("paper", "GLASS"), true);

        Person person = PersonMapper.toEntity(request);

        assertThat(person.getName()).isEqualTo("Collector");
        assertThat(person.getTelephone()).isEqualTo("35999999999");
        assertThat(person.getPersonType()).isEqualTo(PersonType.COLLECTOR);
        assertThat(person.isCollectPoint()).isTrue();
        assertThat(person.getGarbageType()).containsExactly(GarbageType.PAPER, GarbageType.GLASS);
        assertThat(person.getDescription()).isEqualTo("Available on weekdays");
        assertThat(person.getAddress().getCity()).isEqualTo("Itajuba");
        assertThat(person.getId()).isNull();
        assertThat(person.getEmail()).isNull();
        assertThat(person.getPassword()).isNull();
        assertThat(person.getGodfather()).isNull();
        assertThat(person.getCreatedAt()).isNull();
        assertThat(person.getUpdatedAt()).isNull();
    }

    @Test
    void shouldKeepOptionalAddressNullAndAcceptEmptyMaterialsOnUpdate() {
        UpdatePersonRequest request = new UpdatePersonRequest(
                "Collector", null, "COLLECTOR", false, List.of(), null, null
        );

        Person person = PersonMapper.toEntity(request);

        assertThat(person.getAddress()).isNull();
        assertThat(person.getGarbageType()).isEmpty();
    }

    @Test
    void shouldRejectInvalidGarbageTypeWhenValidationIsBypassed() {
        UpdatePersonRequest request = validUpdateRequest(List.of("UNKNOWN"), false);

        assertThatThrownBy(() -> PersonMapper.toEntity(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("invalid garbageType");
    }

    @Test
    void shouldMapPersonToPublicResponseWithoutRecursiveGodfather() {
        PersonResponse response = PersonMapper.toResponse(recycler());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("recycler@example.com");
        assertThat(response.name()).isEqualTo("Recycler");
        assertThat(response.telephone()).isEqualTo("35999999999");
        assertThat(response.personType()).isEqualTo("RECYCLER");
        assertThat(response.garbageType()).containsExactly("PAPER", "GLASS");
        assertThat(response.description()).isEqualTo("Available on weekdays");
        assertThat(response.address().city()).isEqualTo("Itajuba");
        assertThat(response.godfather().id()).isEqualTo(2L);
        assertThat(response.godfather().name()).isEqualTo("Godfather");
        assertThat(response.godfather().personType()).isEqualTo("COLLECTOR");
    }

    @Test
    void shouldMapNullablePersonFields() {
        Person person = recycler();
        person.setGarbageType(null);
        person.setGodfather(null);
        person.setAddress(null);

        PersonResponse response = PersonMapper.toResponse(person);

        assertThat(response.garbageType()).isEmpty();
        assertThat(response.godfather()).isNull();
        assertThat(response.address()).isNull();
    }

    @Test
    void shouldReturnNullForNullPerson() {
        assertThat(PersonMapper.toResponse(null)).isNull();
    }

    private UpdatePersonRequest validUpdateRequest(List<String> garbageTypes, boolean collectPoint) {
        return new UpdatePersonRequest(
                "Collector", "35999999999", "collector", collectPoint, garbageTypes,
                "Available on weekdays", addressRequest()
        );
    }
}
