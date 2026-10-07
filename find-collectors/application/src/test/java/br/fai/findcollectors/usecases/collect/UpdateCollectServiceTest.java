package br.fai.findcollectors.usecases.collect;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.CollectStatus;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.CollectRepository;
import br.fai.findcollectors.valueobject.Address;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UpdateCollectServiceTest {
    private final CollectRepository repository = mock(CollectRepository.class);
    private final UpdateCollectService service = new UpdateCollectService(repository);

    @Test
    void shouldUpdateOnlyEditableFieldsOnExistingInstanceAndReturnRepositoryResult() {
        Person recycler = Person.builder().id(1L).build();
        Person collector = Person.builder().id(2L).build();
        Collect existing = Collect.builder().id(10L).status(CollectStatus.ACCEPTED)
                .recycler(recycler).collector(collector).build();
        LocalDateTime date = LocalDateTime.of(2026, 11, 1, 10, 0);
        Address address = Address.builder().city("Varginha").state("MG").build();
        List<GarbageType> materials = List.of(GarbageType.METAL);
        Collect changes = Collect.builder().id(99L).dateAndTime(date).garbageType(materials)
                .status(CollectStatus.CANCELLED).recurrent(true).address(address)
                .recycler(Person.builder().id(8L).build()).collector(Person.builder().id(9L).build()).build();
        Collect persisted = Collect.builder().id(10L).build();
        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.update(existing)).thenReturn(persisted);

        Collect result = service.execute(10L, changes);

        assertThat(result).isSameAs(persisted);
        assertThat(existing).extracting(Collect::getDateAndTime, Collect::getGarbageType,
                        Collect::isRecurrent, Collect::getAddress)
                .containsExactly(date, materials, true, address);
        assertThat(existing).extracting(Collect::getId, Collect::getStatus,
                        Collect::getRecycler, Collect::getCollector)
                .containsExactly(10L, CollectStatus.ACCEPTED, recycler, collector);
        verify(repository).update(same(existing));
        verify(repository, never()).update(same(changes));
    }

    @Test
    void shouldRejectMissingCollectWithoutWriting() {
        when(repository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.execute(10L, new Collect()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Collect with id 10 not found")
                .extracting("code").isEqualTo(ErrorCode.COLLECT_NOT_FOUND);
        verify(repository, never()).update(any());
    }
}
