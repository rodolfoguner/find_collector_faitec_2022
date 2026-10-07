package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.PersonRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PersonQueryServiceTest {
    private final PersonRepository repository = mock(PersonRepository.class);
    private final PersonQueryService service = new PersonQueryService(repository);

    @Test
    void shouldReturnAllPeople() {
        List<Person> people = List.of(Person.builder().id(1L).build(), Person.builder().id(2L).build());
        when(repository.find()).thenReturn(people);
        assertThat(service.find()).isSameAs(people);
    }

    @Test
    void shouldReturnPersonById() {
        Person person = Person.builder().id(7L).build();
        when(repository.findById(7L)).thenReturn(Optional.of(person));
        assertThat(service.findById(7L)).isSameAs(person);
    }

    @Test
    void shouldRejectMissingPersonById() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(7L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Person with id 7 not found")
                .extracting("code").isEqualTo(ErrorCode.PERSON_NOT_FOUND);
    }

    @Test
    void shouldDelegateEmailLookupIncludingNullResult() {
        when(repository.findPersonByEmail("missing@example.com")).thenReturn(null);
        assertThat(service.findPersonByEmail("missing@example.com")).isNull();
        verify(repository).findPersonByEmail("missing@example.com");
    }

    @Test
    void shouldDelegateGodfatherLookup() {
        List<Person> godchildren = List.of(Person.builder().id(2L).build());
        when(repository.findByGodFatherId(9L)).thenReturn(godchildren);
        assertThat(service.findByGodFatherId(9L)).isSameAs(godchildren);
        verify(repository).findByGodFatherId(9L);
    }
}
