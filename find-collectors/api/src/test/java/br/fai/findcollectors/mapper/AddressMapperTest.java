package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.request.AddressRequest;
import br.fai.findcollectors.dto.response.AddressResponse;
import br.fai.findcollectors.valueobject.Address;
import org.junit.jupiter.api.Test;

import static br.fai.findcollectors.ApiTestFixtures.address;
import static br.fai.findcollectors.ApiTestFixtures.addressRequest;
import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

    @Test
    void shouldMapRequestToEntity() {
        Address mapped = AddressMapper.toEntity(addressRequest());

        assertThat(mapped).usingRecursiveComparison().isEqualTo(address());
    }

    @Test
    void shouldPreserveNullNumberWhenMappingRequest() {
        AddressRequest request = new AddressRequest(
                "37500-000", "Main Street", "Downtown", null, "Itajuba", "Minas Gerais"
        );

        assertThat(AddressMapper.toEntity(request).getNumber()).isNull();
    }

    @Test
    void shouldReturnNullForNullRequest() {
        assertThat(AddressMapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        AddressResponse response = AddressMapper.toResponse(address());

        assertThat(response).isEqualTo(new AddressResponse(
                "37500-000", "Main Street", "Downtown", "10", "Itajuba", "Minas Gerais"
        ));
    }

    @Test
    void shouldPreserveNullNumberWhenMappingResponse() {
        Address source = address();
        source.setNumber(null);

        assertThat(AddressMapper.toResponse(source).number()).isNull();
    }

    @Test
    void shouldReturnNullForNullEntity() {
        assertThat(AddressMapper.toResponse(null)).isNull();
    }
}
