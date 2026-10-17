package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.valueobject.Address;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UpdatePersonServiceTest {
    private final PersonRepository repository = mock(PersonRepository.class);
    private final UpdatePersonService service = new UpdatePersonService(repository);

    @Test
    void shouldUpdateOnlyEditableFieldsOnExistingInstanceAndReturnRepositoryResult() {
        Person godfather = Person.builder().id(3L).build();
        Person existing = Person.builder().id(1L).email("old@example.com").password("encoded")
                .name("Old").telephone("111").personType(PersonType.RECYCLER).godfather(godfather)
                .collectPoint(false).garbageType(List.of(GarbageType.PAPER)).description("old").build();
        Address address = Address.builder().city("New city").state("MG").build();
        Person changes = Person.builder().id(99L).email("new@example.com").password("new password")
                .name("New").telephone("222").address(address).personType(PersonType.COLLECTOR)
                .godfather(Person.builder().id(8L).build()).collectPoint(true)
                .garbageType(List.of(GarbageType.GLASS, GarbageType.METAL)).description("new").build();
        Person persisted = Person.builder().id(1L).name("persisted").build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.update(existing)).thenReturn(persisted);

        Person result = service.execute(1L, changes);

        assertThat(result).isSameAs(persisted);
        assertThat(existing).extracting(Person::getName, Person::getTelephone, Person::getAddress,
                        Person::getPersonType, Person::isCollectPoint, Person::getGarbageType, Person::getDescription)
                .containsExactly("New", "222", address, PersonType.COLLECTOR, true,
                        List.of(GarbageType.GLASS, GarbageType.METAL), "new");
        assertThat(existing).extracting(Person::getId, Person::getEmail, Person::getPassword, Person::getGodfather)
                .containsExactly(1L, "old@example.com", "encoded", godfather);
        verify(repository).update(same(existing));
        verify(repository, never()).update(same(changes));
    }

    @Test
    void shouldRejectMissingPersonWithoutWriting() {
        when(repository.findById(4L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.execute(4L, new Person()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Person with id 4 not found")
                .extracting("code").isEqualTo(ErrorCode.PERSON_NOT_FOUND);
        verify(repository, never()).update(any());
    }
}
