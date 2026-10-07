package br.fai.findcollectors.usecases.auth;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.InvalidCredentialsException;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthenticateUserServiceTest {
    private final PersonRepository repository = mock(PersonRepository.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private final AuthenticateUserService service = new AuthenticateUserService(repository, passwordHasher);

    @Test
    void shouldReturnPersonWhenCredentialsMatch() {
        Person person = Person.builder().email("person@example.com").password("encoded").build();
        when(repository.findPersonByEmail(person.getEmail())).thenReturn(person);
        when(passwordHasher.matches("RawPassword1", "encoded")).thenReturn(true);

        assertThat(service.execute(person.getEmail(), "RawPassword1")).isSameAs(person);
        verify(passwordHasher).matches("RawPassword1", "encoded");
    }

    @Test
    void shouldRejectUnknownEmailWithoutConsultingHasher() {
        when(repository.findPersonByEmail("unknown@example.com")).thenReturn(null);

        assertInvalidCredentials(() -> service.execute("unknown@example.com", "password"));
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void shouldRejectWrongPasswordWithoutChangingOrPersistingPerson() {
        Person person = Person.builder().id(1L).email("person@example.com").password("encoded").build();
        when(repository.findPersonByEmail(person.getEmail())).thenReturn(person);
        when(passwordHasher.matches("wrong", "encoded")).thenReturn(false);

        assertInvalidCredentials(() -> service.execute(person.getEmail(), "wrong"));
        assertThat(person.getPassword()).isEqualTo("encoded");
        verify(repository, never()).update(any());
        verify(repository, never()).create(any());
    }

    private static void assertInvalidCredentials(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Email or password is invalid")
                .extracting("code").isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }
}
