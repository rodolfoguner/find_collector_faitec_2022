package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PersonQueryService implements PersonQueryUseCase{

    private final PersonRepository repository;

    @Override
    public List<Person> find() {
        return repository.find();
    }

    @Override
    public Person findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                    ErrorCode.PERSON_NOT_FOUND,
                    "Person with id %d not found".formatted(id)
                ));
    }

    @Override
    public Person findPersonByEmail(String email) {
        return repository.findPersonByEmail(email);
    }

    @Override
    public List<Person> findByGodFatherId(Long id) {
        return repository.findByGodFatherId(id);
    }
}
