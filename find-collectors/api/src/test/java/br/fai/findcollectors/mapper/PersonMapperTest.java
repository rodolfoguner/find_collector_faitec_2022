package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.request.AddressRequest;
import br.fai.findcollectors.dto.request.UpdatePersonRequest;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonMapperTest {

    @Test
    void shouldMapUpdateRequestWithoutAcceptingImmutableOrSensitiveFields() {
        UpdatePersonRequest request = new UpdatePersonRequest(
                "Collector",
                "35999999999",
                "collector",
                true,
                List.of("paper", "glass"),
                "Available on weekdays",
                new AddressRequest(
                        "37500-000",
                        "Main Street",
                        "Downtown",
                        "10",
                        "Itajuba",
                        "Minas Gerais"
                )
        );

        Person person = PersonMapper.toEntity(request);

        assertThat(person.getName()).isEqualTo("Collector");
        assertThat(person.getTelephone()).isEqualTo("35999999999");
        assertThat(person.getPersonType()).isEqualTo(PersonType.COLLECTOR);
        assertThat(person.isCollectPoint()).isTrue();
        assertThat(person.getGarbageType()).containsExactly(GarbageType.PAPER, GarbageType.GLASS);
        assertThat(person.getDescription()).isEqualTo("Available on weekdays");
        assertThat(person.getAddress().getCity()).isEqualTo("Itajuba");
        assertThat(person.getId()).isNull();
        assertThat(person.getEmail()).isNull();
        assertThat(person.getPassword()).isNull();
        assertThat(person.getGodfather()).isNull();
    }
}
