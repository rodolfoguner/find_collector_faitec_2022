package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.request.CreateCollectRequest;
import br.fai.findcollectors.dto.request.UpdateCollectRequest;
import br.fai.findcollectors.dto.response.CollectResponse;
import br.fai.findcollectors.dto.response.PersonSummaryResponse;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;

import java.util.List;

public final class CollectMapper {

    private CollectMapper() {
    }

    public static Collect toEntity(CreateCollectRequest request) {
        return Collect.builder()
                .dateAndTime(request.dateAndTime())
                .garbageType(toGarbageTypes(request.garbageType()))
                .recurrent(request.recurrent())
                .address(AddressMapper.toEntity(request.address()))
                .recycler(Person.builder().id(request.recyclerId()).build())
                .build();
    }

    public static Collect toEntity(UpdateCollectRequest request) {
        return Collect.builder()
                .dateAndTime(request.dateAndTime())
                .garbageType(toGarbageTypes(request.garbageType()))
                .recurrent(request.recurrent())
                .address(AddressMapper.toEntity(request.address()))
                .build();
    }

    public static CollectResponse toResponse(Collect collect) {
        return CollectResponse.builder()
                .id(collect.getId())
                .dateAndTime(collect.getDateAndTime())
                .garbageType(collect.getGarbageType() == null ? List.of() : collect.getGarbageType()
                        .stream()
                        .map(GarbageType::name)
                        .toList())
                .status(collect.getStatus().name())
                .recurrent(collect.isRecurrent())
                .address(AddressMapper.toResponse(collect.getAddress()))
                .collector(toPersonSummary(collect.getCollector()))
                .recycler(toPersonSummary(collect.getRecycler()))
                .createdAt(collect.getCreatedAt())
                .updatedAt(collect.getUpdatedAt())
                .build();
    }

    private static List<GarbageType> toGarbageTypes(List<String> values) {
        return values.stream()
                .map(GarbageType::fromString)
                .toList();
    }

    private static PersonSummaryResponse toPersonSummary(Person person) {
        if (person == null) {
            return null;
        }

        return PersonSummaryResponse.builder()
                .id(person.getId())
                .name(person.getName())
                .personType(person.getPersonType().name())
                .build();
    }
}
