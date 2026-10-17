package br.fai.findcollectors.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static br.fai.findcollectors.ApiTestFixtures.addressRequest;

class PersonRequestValidationTest extends ValidationTestSupport {

    @ParameterizedTest
    @ValueSource(strings = {"RECYCLER", "recycler", "COLLECTOR", "collector"})
    void shouldAcceptValidCreatePersonRolesIgnoringCase(String role) {
        assertValid(new CreatePersonRequest("person@example.com", "Password1", "Person", role));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingCreateFields(String value) {
        assertViolation(new CreatePersonRequest(value, "Password1", "Person", "RECYCLER"),
                "email", "email is required");
        assertViolation(new CreatePersonRequest("person@example.com", value, "Person", "RECYCLER"),
                "password", "password is required");
        assertViolation(new CreatePersonRequest("person@example.com", "Password1", value, "RECYCLER"),
                "name", "name is required");
        assertViolation(new CreatePersonRequest("person@example.com", "Password1", "Person", value),
                "personType", "personType is required");
    }

    @Test
    void shouldValidateCreateEmailAndBoundaries() {
        assertViolation(validCreate("invalid", "Password1", "Person", "RECYCLER"),
                "email", "email needs to be valid");
        assertValid(validCreate("x".repeat(64) + "@" + "d".repeat(31) + ".com",
                "Password1", "Person", "RECYCLER"));
        assertViolation(validCreate("x".repeat(64) + "@" + "d".repeat(32) + ".com",
                        "Password1", "Person", "RECYCLER"),
                "email", "email must have at most 100 characters");
    }

    @Test
    void shouldValidateCreatePasswordPolicyAndBoundaries() {
        assertValid(validCreate("A1xxxxxx", "Person"));
        assertValid(validCreate("A1" + text(98), "Person"));
        assertViolation(validCreate("A1xxxxx", "Person"),
                "password", "password must have between 8 and 100 characters");
        assertViolation(validCreate("A1" + text(99), "Person"),
                "password", "password must have between 8 and 100 characters");
        assertViolation(validCreate("Password", "Person"),
                "password", "password must contain at least one number");
        assertViolation(validCreate("password1", "Person"),
                "password", "password must contain at least one uppercase letter");
    }

    @Test
    void shouldValidateCreateNameAndRole() {
        assertValid(validCreate("Password1", text(100)));
        assertViolation(validCreate("Password1", text(101)),
                "name", "name must have at most 100 characters");
        assertViolation(validCreate("person@example.com", "Password1", "Person", "ADMIN"),
                "personType", "personType must be RECYCLER or COLLECTOR");
    }

    @Test
    void shouldAcceptCompleteUpdateAndOptionalFields() {
        assertValid(validUpdate(List.of("paper", "GLASS"), addressRequest(), "359999999999999"));
        assertValid(validUpdate(List.of(), null, null));
        assertValid(new UpdatePersonRequest(
                "Collector", null, "collector", true, List.of("paper"), null, addressRequest()
        ));
    }

    @Test
    void shouldValidateUpdateNameTelephoneAndRole() {
        assertViolation(new UpdatePersonRequest(
                        " ", null, "COLLECTOR", false, List.of(), null, null),
                "name", "name is required");
        assertViolation(new UpdatePersonRequest(
                        text(101), null, "COLLECTOR", false, List.of(), null, null),
                "name", "name must have at most 100 characters");
        assertViolation(validUpdate(List.of(), null, text(16)),
                "telephone", "telephone must have at most 15 characters");
        assertViolation(new UpdatePersonRequest(
                        "Collector", null, "ADMIN", false, List.of(), null, null),
                "personType", "personType must be RECYCLER or COLLECTOR");
        assertViolation(new UpdatePersonRequest(
                        "Collector", null, null, false, List.of(), null, null),
                "personType", "personType is required");
    }

    @Test
    void shouldValidateUpdateMaterialsAndNestedAddress() {
        assertViolation(validUpdate(null, null, null), "garbageType", "garbageType is required");
        assertViolation(validUpdate(List.of(""), null, null),
                "garbageType[0].<list element>", "garbageType must not contain blank values");
        assertViolation(validUpdate(List.of("UNKNOWN"), null, null),
                "garbageType[0].<list element>", "garbageType contains an invalid value");

        List<String> withNull = new java.util.ArrayList<>();
        withNull.add(null);
        assertViolation(validUpdate(withNull, null, null),
                "garbageType[0].<list element>", "garbageType must not contain blank values");

        AddressRequest invalidAddress = new AddressRequest(" ", "Street", "District", null, "City", "State");
        assertViolation(validUpdate(List.of("paper"), invalidAddress, null),
                "address.cep", "cep is required");
    }

    private CreatePersonRequest validCreate(String password, String name) {
        return validCreate("person@example.com", password, name, "RECYCLER");
    }

    private CreatePersonRequest validCreate(String email, String password, String name, String role) {
        return new CreatePersonRequest(email, password, name, role);
    }

    private UpdatePersonRequest validUpdate(List<String> materials, AddressRequest address, String telephone) {
        return new UpdatePersonRequest(
                "Collector", telephone, "collector", true, materials, "Description", address
        );
    }
}
