package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DeletePersonService implements DeletePersonUseCase{

    private final PersonRepository repository;

    @Override
    public void execute(Long id) {

        Optional<Person> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException();
        }

        repository.deleteById(id);
    }
}
