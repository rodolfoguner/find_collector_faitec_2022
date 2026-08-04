package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonQueryService implements PersonQueryUseCase{

    private final PersonRepository repository;

    @Override
    public List<Person> find() {
        return repository.find();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return repository.findById(id);
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
