package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.response.AddressResponse;
import br.fai.findcollectors.valueobject.Address;

public class AddressMapper {
    
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
