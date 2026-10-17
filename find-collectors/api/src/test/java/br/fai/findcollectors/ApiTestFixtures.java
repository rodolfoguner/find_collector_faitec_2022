package br.fai.findcollectors;

import br.fai.findcollectors.dto.request.AddressRequest;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.CollectStatus;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.valueobject.Address;

import java.time.LocalDateTime;
import java.util.List;

public final class ApiTestFixtures {

    public static final LocalDateTime COLLECT_DATE = LocalDateTime.of(2026, 10, 7, 14, 30);
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 10, 1, 9, 15);
    public static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 10, 2, 10, 45);

    private ApiTestFixtures() {
    }

    public static AddressRequest addressRequest() {
        return new AddressRequest(
                "37500-000", "Main Street", "Downtown", "10", "Itajuba", "Minas Gerais"
        );
    }

    public static Address address() {
        return Address.builder()
                .cep("37500-000")
                .street("Main Street")
                .district("Downtown")
                .number("10")
                .city("Itajuba")
                .state("Minas Gerais")
                .build();
    }

    public static Person recycler() {
        Person godfather = Person.builder()
                .id(2L)
                .email("private-godfather@example.com")
                .password("Private2")
                .name("Godfather")
                .personType(PersonType.COLLECTOR)
                .address(address())
                .build();

        Person person = Person.builder()
                .id(1L)
                .email("recycler@example.com")
                .password("Private1")
                .name("Recycler")
                .telephone("35999999999")
                .personType(PersonType.RECYCLER)
                .godfather(godfather)
                .garbageType(List.of(GarbageType.PAPER, GarbageType.GLASS))
                .description("Available on weekdays")
                .collectPoint(true)
                .address(address())
                .build();
        person.setCreatedAt(CREATED_AT);
        person.setUpdatedAt(UPDATED_AT);
        return person;
    }

    public static Person collector() {
        return Person.builder()
                .id(3L)
                .email("private-collector@example.com")
                .password("Private3")
                .name("Collector")
                .personType(PersonType.COLLECTOR)
                .address(address())
                .build();
    }

    public static Collect collect() {
        Collect collect = Collect.builder()
                .id(4L)
                .dateAndTime(COLLECT_DATE)
                .garbageType(List.of(GarbageType.PAPER, GarbageType.METAL))
                .status(CollectStatus.ACCEPTED)
                .recurrent(true)
                .address(address())
                .collector(collector())
                .recycler(recycler())
                .build();
        collect.setCreatedAt(CREATED_AT);
        collect.setUpdatedAt(UPDATED_AT);
        return collect;
    }
}
