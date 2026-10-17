package br.fai.findcollectors.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static br.fai.findcollectors.ApiTestFixtures.addressRequest;

class AddressRequestValidationTest extends ValidationTestSupport {

    @Test
    void shouldAcceptValidAddress() {
        assertValid(addressRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingRequiredFields(String value) {
        assertViolation(withCep(value), "cep", "cep is required");
        assertViolation(withStreet(value), "street", "street is required");
        assertViolation(withDistrict(value), "district", "district is required");
        assertViolation(withCity(value), "city", "city is required");
        assertViolation(withState(value), "state", "state is required");
    }

    @Test
    void shouldAcceptExactLimitsAndNullNumber() {
        assertValid(new AddressRequest(text(10), text(100), text(100), text(10), text(100), text(50)));
        assertValid(new AddressRequest("37500-000", "Street", "District", null, "City", "State"));
    }

    @Test
    void shouldRejectValuesAboveLimitsWithPublicMessages() {
        assertViolation(withCep(text(11)), "cep", "cep must have at most 10 characters");
        assertViolation(withStreet(text(101)), "street", "street must have at most 100 characters");
        assertViolation(withDistrict(text(101)), "district", "district must have at most 100 characters");
        assertViolation(withNumber(text(11)), "number", "number must have at most 10 characters");
        assertViolation(withCity(text(101)), "city", "city must have at most 100 characters");
        assertViolation(withState(text(51)), "state", "state must have at most 50 characters");
    }

    private AddressRequest withCep(String value) {
        AddressRequest valid = addressRequest();
        return new AddressRequest(value, valid.street(), valid.district(), valid.number(), valid.city(), valid.state());
    }

    private AddressRequest withStreet(String value) {
        AddressRequest valid = addressRequest();
        return new AddressRequest(valid.cep(), value, valid.district(), valid.number(), valid.city(), valid.state());
    }

    private AddressRequest withDistrict(String value) {
        AddressRequest valid = addressRequest();
        return new AddressRequest(valid.cep(), valid.street(), value, valid.number(), valid.city(), valid.state());
    }

    private AddressRequest withNumber(String value) {
        AddressRequest valid = addressRequest();
        return new AddressRequest(valid.cep(), valid.street(), valid.district(), value, valid.city(), valid.state());
    }

    private AddressRequest withCity(String value) {
        AddressRequest valid = addressRequest();
        return new AddressRequest(valid.cep(), valid.street(), valid.district(), valid.number(), value, valid.state());
    }

    private AddressRequest withState(String value) {
        AddressRequest valid = addressRequest();
        return new AddressRequest(valid.cep(), valid.street(), valid.district(), valid.number(), valid.city(), value);
    }
}
