package br.fai.findcollectors.usecases.collect;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.repositories.CollectRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateCollectService implements CreateCollectUseCase {
    
    private final CollectRepository repository;
    private final PersonRepository personRepository;

    @Override
    public Collect execute(Collect collect) {
            
        Optional<Person> recycler = personRepository.findById(collect.getRecycler().getId());
        if (recycler.isEmpty()) {
            throw new RuntimeException();
        }

       return repository.create(collect);
    }
}
