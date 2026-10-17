package br.fai.findcollectors.controller;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.CollectStatus;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.valueobject.Address;

import java.time.LocalDateTime;
import java.util.List;

final class ControllerTestFixtures {

    static final String ADDRESS_JSON = """
            {"cep":"37500-000","street":"Main Street","district":"Center",
             "number":"10","city":"Itajuba","state":"MG"}
            """;

    private ControllerTestFixtures() {
    }

    static Person recycler(long id) {
        Person person = Person.builder()
                .id(id)
                .email("recycler@example.com")
                .password("MustNotLeak1")
                .name("Recycler")
                .telephone("35999999999")
                .personType(PersonType.RECYCLER)
                .garbageType(List.of(GarbageType.PAPER, GarbageType.GLASS))
                .description("Collect point")
                .address(address())
                .build();
        person.setCreatedAt(LocalDateTime.of(2026, 10, 1, 9, 0));
        person.setUpdatedAt(LocalDateTime.of(2026, 10, 2, 10, 0));
        return person;
    }

    static Person collector(long id) {
        return Person.builder()
                .id(id)
                .email("collector@example.com")
                .password("AlsoMustNotLeak1")
                .name("Collector")
                .personType(PersonType.COLLECTOR)
                .build();
    }

    static Collect collect(long id) {
        Collect collect = Collect.builder()
                .id(id)
                .dateAndTime(LocalDateTime.of(2026, 10, 20, 14, 30))
                .garbageType(List.of(GarbageType.PAPER, GarbageType.GLASS))
                .status(CollectStatus.PENDING)
                .recurrent(true)
                .address(address())
                .recycler(recycler(11L))
                .collector(null)
                .build();
        collect.setCreatedAt(LocalDateTime.of(2026, 10, 1, 9, 0));
        collect.setUpdatedAt(LocalDateTime.of(2026, 10, 2, 10, 0));
        return collect;
    }

    static Address address() {
        return Address.builder()
                .cep("37500-000")
                .street("Main Street")
                .district("Center")
                .number("10")
                .city("Itajuba")
                .state("MG")
                .build();
    }
}
