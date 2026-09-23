package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.request.CreatePersonRequest;
import br.fai.findcollectors.dto.request.UpdatePersonRequest;
import br.fai.findcollectors.dto.response.PersonResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;

import java.util.List;

public class PersonMapper {
 
    public static Person toEntity(CreatePersonRequest request) {
        
        return Person.builder()
                .email(request.email())
                .name(request.name())
                .password(request.password())
                .personType(PersonType.fromString(request.personType()))
                .build();
    }

    public static Person toEntity(UpdatePersonRequest request) {

        return Person.builder()
                .name(request.name())
                .telephone(request.telephone())
                .personType(PersonType.fromString(request.personType()))
                .collectPoint(request.collectPoint())
                .garbageType(request.garbageType()
                        .stream()
                        .map(GarbageType::fromString)
                        .toList())
                .description(request.description())
                .address(AddressMapper.toEntity(request.address()))
                .build();
    }

    public static PersonResponse toResponse(Person person) {

        if (person == null) {
            return null;
        }

        return PersonResponse.builder()
                .id(person.getId())
                .email(person.getEmail())
                .name(person.getName())
                .telephone(person.getTelephone())
                .personType(person.getPersonType().name())
                .godfather(toResponse(person.getGodfather()))
                .garbageType(person.getGarbageType() == null ? List.of() : person.getGarbageType()
                        .stream()
                        .map(GarbageType::name)
                        .toList()
                )
                .description(person.getDescription())
                .address(AddressMapper.toResponse(person.getAddress()))
                .build();
    }
}
