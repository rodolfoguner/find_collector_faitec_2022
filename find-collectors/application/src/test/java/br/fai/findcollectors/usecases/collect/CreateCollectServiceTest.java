package br.fai.findcollectors.usecases.collect;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.CollectStatus;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.CollectRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.valueobject.Address;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CreateCollectServiceTest {
    private final CollectRepository repository = mock(CollectRepository.class);
    private final PersonRepository personRepository = mock(PersonRepository.class);
    private final CreateCollectService service = new CreateCollectService(repository, personRepository);

    @Test
    void shouldLoadRecyclerNormalizeCollectPersistSameInstanceAndReturnResult() {
        Person partialRecycler = Person.builder().id(12L).build();
        Person loadedRecycler = Person.builder().id(12L).email("recycler@example.com")
                .personType(PersonType.RECYCLER).build();
        Person improperCollector = Person.builder().id(30L).build();
        LocalDateTime date = LocalDateTime.of(2026, 10, 20, 14, 30);
        Address address = Address.builder().city("Lavras").state("MG").build();
        List<GarbageType> materials = List.of(GarbageType.PAPER, GarbageType.GLASS);
        Collect request = Collect.builder().id(44L).dateAndTime(date).garbageType(materials)
                .status(CollectStatus.COMPLETED).recurrent(true).address(address)
                .collector(improperCollector).recycler(partialRecycler).build();
        Collect created = Collect.builder().id(100L).build();
        when(personRepository.findById(12L)).thenReturn(Optional.of(loadedRecycler));
        when(repository.create(request)).thenReturn(created);

        Collect result = service.execute(request);

        assertThat(result).isSameAs(created);
        assertThat(request.getRecycler()).isSameAs(loadedRecycler);
        assertThat(request.getCollector()).isNull();
        assertThat(request.getStatus()).isEqualTo(CollectStatus.PENDING);
        assertThat(request).extracting(Collect::getDateAndTime, Collect::getAddress,
                        Collect::getGarbageType, Collect::isRecurrent)
                .containsExactly(date, address, materials, true);
        verify(repository).create(same(request));
    }

    @Test
    void shouldRejectMissingRecyclerWithoutCreating() {
        Collect request = Collect.builder().recycler(Person.builder().id(12L).build()).build();
        when(personRepository.findById(12L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Recycler with id 12 not found")
                .extracting("code").isEqualTo(ErrorCode.RECYCLER_NOT_FOUND);
        verify(repository, never()).create(any());
    }

    @Test
    void shouldRejectPersonWhoIsNotRecyclerWithoutCreating() {
        Collect request = Collect.builder().recycler(Person.builder().id(12L).build()).build();
        when(personRepository.findById(12L)).thenReturn(Optional.of(
                Person.builder().id(12L).personType(PersonType.COLLECTOR).build()));

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Recycler must be of type recycler.")
                .extracting("code").isEqualTo(ErrorCode.BUSINESS_RULE_VIOLATION);
        verify(repository, never()).create(any());
    }
}
