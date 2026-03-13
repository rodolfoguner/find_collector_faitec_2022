package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreatePersonService implements CreatePersonUseCase {

    private final PersonRepository repository;

    @Override
    public Person execute(Person person) {

        if (repository.findPersonByEmail(person.getEmail()) != null) {
            throw new RuntimeException();
        }

        return repository.create(person);
    }
}
