package br.fai.findcollectors.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PersonResponse(
    
    Long id,
    String email,
    String name,
    String telephone,
    String personType,
    PersonResponse godfather,
    List<String> garbageType,
    String description,
    AddressResponse address
) {}
