package br.fai.findcollectors.dto.request;

import br.fai.findcollectors.enums.GarbageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.fai.findcollectors.ApiTestFixtures.COLLECT_DATE;
import static br.fai.findcollectors.ApiTestFixtures.addressRequest;
import static org.assertj.core.api.Assertions.assertThat;

class CollectRequestValidationTest extends ValidationTestSupport {

    @Test
    void shouldAcceptCompleteCreateWithEitherRecurrenceValue() {
        assertValid(validCreate(List.of("paper", "GLASS"), false, addressRequest(), 1L));
        assertValid(validCreate(List.of("paper"), true, addressRequest(), 1L));
    }

    @ParameterizedTest
    @EnumSource(GarbageType.class)
    void shouldAcceptEveryMaterialIgnoringCase(GarbageType material) {
        assertValid(validCreate(List.of(material.name().toLowerCase()), false, addressRequest(), 1L));
        assertValid(validUpdate(List.of(material.name().toLowerCase()), addressRequest()));
    }

    @Test
    void shouldRejectInvalidCreateFields() {
        assertViolation(new CreateCollectRequest(null, List.of("PAPER"), false, addressRequest(), 1L),
                "dateAndTime", "dateAndTime is required");
        assertViolation(validCreate(null, false, addressRequest(), 1L),
                "garbageType", "garbageType is required");
        assertViolation(validCreate(List.of(), false, addressRequest(), 1L),
                "garbageType", "garbageType is required");
        assertViolation(validCreate(List.of(""), false, addressRequest(), 1L),
                "garbageType[0].<list element>", "garbageType must not contain blank values");
        assertViolation(validCreate(List.of("UNKNOWN"), false, addressRequest(), 1L),
                "garbageType[0].<list element>", "garbageType contains an invalid value");
        assertViolation(validCreate(List.of("PAPER"), false, null, 1L),
                "address", "address is required");
        assertViolation(validCreate(List.of("PAPER"), false, addressRequest(), null),
                "recyclerId", "recyclerId is required");
        assertViolation(validCreate(List.of("PAPER"), false, addressRequest(), 0L),
                "recyclerId", "recyclerId must be positive");
        assertViolation(validCreate(List.of("PAPER"), false, addressRequest(), -1L),
                "recyclerId", "recyclerId must be positive");
    }

    @Test
    void shouldRejectNullMaterialAndInvalidNestedCreateAddress() {
        List<String> materials = new ArrayList<>();
        materials.add(null);
        assertViolation(validCreate(materials, false, addressRequest(), 1L),
                "garbageType[0].<list element>", "garbageType must not contain null values");

        AddressRequest invalid = new AddressRequest("", "Street", "District", null, "City", "State");
        assertViolation(validCreate(List.of("PAPER"), false, invalid, 1L),
                "address.cep", "cep is required");
    }

    @Test
    void shouldValidateUpdateFieldsAndNestedAddress() {
        assertValid(validUpdate(List.of("paper"), addressRequest()));
        assertViolation(new UpdateCollectRequest(null, List.of("PAPER"), false, addressRequest()),
                "dateAndTime", "dateAndTime is required");
        assertViolation(validUpdate(null, addressRequest()), "garbageType", "garbageType is required");
        assertViolation(validUpdate(List.of(), addressRequest()), "garbageType", "garbageType is required");
        assertViolation(validUpdate(List.of(""), addressRequest()),
                "garbageType[0].<list element>", "garbageType must not contain blank values");
        assertViolation(validUpdate(List.of("UNKNOWN"), addressRequest()),
                "garbageType[0].<list element>", "garbageType contains an invalid value");
        assertViolation(validUpdate(List.of("PAPER"), null), "address", "address is required");

        AddressRequest invalid = new AddressRequest("CEP", "", "District", null, "City", "State");
        assertViolation(validUpdate(List.of("PAPER"), invalid), "address.street", "street is required");
    }

    @Test
    void shouldRejectNullMaterialOnUpdate() {
        List<String> materials = new ArrayList<>();
        materials.add(null);

        assertViolation(validUpdate(materials, addressRequest()),
                "garbageType[0].<list element>", "garbageType must not contain null values");
    }

    @Test
    void updateContractShouldNotExposeLifecycleOrOwnershipFields() {
        assertThat(Arrays.stream(UpdateCollectRequest.class.getRecordComponents())
                .map(RecordComponent::getName))
                .containsExactly("dateAndTime", "garbageType", "recurrent", "address")
                .doesNotContain("id", "status", "recycler", "collector");
    }

    private CreateCollectRequest validCreate(
            List<String> materials, boolean recurrent, AddressRequest address, Long recyclerId
    ) {
        return new CreateCollectRequest(COLLECT_DATE, materials, recurrent, address, recyclerId);
    }

    private UpdateCollectRequest validUpdate(List<String> materials, AddressRequest address) {
        return new UpdateCollectRequest(COLLECT_DATE, materials, false, address);
    }
}
