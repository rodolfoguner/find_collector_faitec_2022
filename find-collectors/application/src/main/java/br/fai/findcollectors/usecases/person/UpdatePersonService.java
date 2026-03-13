package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdatePersonService implements UpdatePersonUseCase{

    private final PersonRepository repository;

    @Override
    public Person execute(Long id, Person person) {

        Optional<Person> existingDb = repository.findById(id);
        if (existingDb.isEmpty()) {
            throw new RuntimeException();
        }

        Person existing = existingDb.get();

        existing.setName(person.getName());
        existing.setTelephone(person.getTelephone());
        existing.setCep(person.getCep());
        existing.setAddress(person.getAddress());
        existing.setDistrict(person.getDistrict());
        existing.setNumber(person.getNumber());
        existing.setCity(person.getCity());
        existing.setPersonType(person.getPersonType());
        existing.setCollectPoint(person.isCollectPoint());
        existing.setGarbageType(person.getGarbageType());
        existing.setDescription(person.getDescription());

        return repository.update(existing);
    }
}
