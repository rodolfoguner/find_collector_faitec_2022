package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.ConflictException;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CreatePersonServiceTest {
    private final PersonRepository repository = mock(PersonRepository.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private final CreatePersonService service = new CreatePersonService(repository, passwordHasher);

    @Test
    void shouldHashPasswordPersistSamePersonAndReturnRepositoryResult() {
        Person person = Person.builder().email("person@example.com").password("RawPassword1").build();
        Person created = Person.builder().id(1L).email(person.getEmail()).password("encoded").build();
        when(repository.findPersonByEmail(person.getEmail())).thenReturn(null);
        when(passwordHasher.hash("RawPassword1")).thenReturn("encoded");
        when(repository.create(person)).thenReturn(created);

        Person result = service.execute(person);

        assertThat(result).isSameAs(created);
        assertThat(person.getPassword()).isEqualTo("encoded");
        verify(repository).create(same(person));
    }

    @Test
    void shouldRejectRegisteredEmailWithoutHashingOrWriting() {
        Person person = Person.builder().email("person@example.com").password("RawPassword1").build();
        when(repository.findPersonByEmail(person.getEmail())).thenReturn(Person.builder().id(1L).build());

        assertThatThrownBy(() -> service.execute(person))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already registered")
                .extracting("code").isEqualTo(ErrorCode.EMAIL_ALREADY_REGISTERED);
        verifyNoInteractions(passwordHasher);
        verify(repository, never()).create(any());
    }
}
