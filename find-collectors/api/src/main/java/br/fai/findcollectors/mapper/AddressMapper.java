package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.response.AddressResponse;
import br.fai.findcollectors.dto.request.AddressRequest;
import br.fai.findcollectors.valueobject.Address;

public class AddressMapper {

    private AddressMapper() {
    }

    public static Address toEntity(AddressRequest request) {
        if (request == null) {
            return null;
        }

        return Address.builder()
                .cep(request.cep())
                .street(request.street())
                .district(request.district())
                .number(request.number())
                .city(request.city())
                .state(request.state())
                .build();
    }
    
    public static AddressResponse toResponse(Address address) {

        if (address == null) {
            return null;
        }

        return AddressResponse.builder()
                .cep(address.getCep())
                .street(address.getStreet())
                .district(address.getDistrict())
                .number(address.getNumber())
                .city(address.getCity())
                .state(address.getState())
                .build();
    }
}
