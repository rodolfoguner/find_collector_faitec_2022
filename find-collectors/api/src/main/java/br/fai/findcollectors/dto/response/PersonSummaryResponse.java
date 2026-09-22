package br.fai.findcollectors.dto.response;

import lombok.Builder;

@Builder
public record PersonSummaryResponse(

    Long id,
    String name,
    String personType
) {}
