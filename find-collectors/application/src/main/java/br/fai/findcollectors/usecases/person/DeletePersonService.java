package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeletePersonService implements DeletePersonUseCase{

    private final PersonRepository repository;

    @Override
    public void execute(Long id) {

        Person person = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(
                ErrorCode.PERSON_NOT_FOUND, 
                "Person with id %d not found".formatted(id))
            );
        repository.deleteById(person.getId());
    }
}
