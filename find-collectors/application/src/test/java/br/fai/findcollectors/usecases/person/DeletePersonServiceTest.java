package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.PersonRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeletePersonServiceTest {
    private final PersonRepository repository = mock(PersonRepository.class);
    private final DeletePersonService service = new DeletePersonService(repository);

    @Test
    void shouldDeleteUsingFoundPersonId() {
        when(repository.findById(20L)).thenReturn(Optional.of(Person.builder().id(21L).build()));
        service.execute(20L);
        verify(repository).deleteById(21L);
    }

    @Test
    void shouldRejectMissingPersonWithoutDeleting() {
        when(repository.findById(20L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.execute(20L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Person with id 20 not found")
                .extracting("code").isEqualTo(ErrorCode.PERSON_NOT_FOUND);
        verify(repository, never()).deleteById(anyLong());
    }
}
