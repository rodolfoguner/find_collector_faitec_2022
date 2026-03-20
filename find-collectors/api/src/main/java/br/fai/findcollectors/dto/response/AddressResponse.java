package br.fai.findcollectors.dto.response;

import lombok.Builder;

@Builder
public record AddressResponse(
    
    String cep,
    String street,
    String district,
    String number,
    String city,
    String state
) {}
