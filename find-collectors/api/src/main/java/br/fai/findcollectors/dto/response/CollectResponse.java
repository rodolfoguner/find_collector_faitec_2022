package br.fai.findcollectors.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CollectResponse(

    Long id,
    LocalDateTime dateAndTime,
    List<String> garbageType,
    String status,
    boolean recurrent,
    AddressResponse address,
    PersonSummaryResponse collector,
    PersonSummaryResponse recycler,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
