package br.fai.findcollectors.usecases.auth;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.PasswordHasher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final PersonRepository repository;
    private final PasswordHasher passwordHasher;

    @Override
    public Person execute(String email, String password) {

        Person person = repository.findPersonByEmail(email);
        if (person == null) {
            throw new RuntimeException();
        }

        if (!passwordHasher.matches(password, person.getPassword())) {
            throw new RuntimeException();
        }

        return person;
    }
}
