package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.PasswordHasher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreatePersonService implements CreatePersonUseCase {

    private final PersonRepository repository;
    private final PasswordHasher passwordHasher;

    @Override
    public Person execute(Person person) {

        if (repository.findPersonByEmail(person.getEmail()) != null) {
            throw new RuntimeException();
        }

        person.setPassword(passwordHasher.hash(person.getPassword()));

        return repository.create(person);
    }
}
