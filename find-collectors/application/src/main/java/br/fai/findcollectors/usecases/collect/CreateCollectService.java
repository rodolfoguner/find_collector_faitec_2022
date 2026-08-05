package br.fai.findcollectors.usecases.collect;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
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
            
        Person recycler = personRepository.findById(collect.getRecycler().getId())
                .orElseThrow(() -> new NotFoundException(
                    ErrorCode.RECYCLER_NOT_FOUND, 
                    "Recycler with id %d not found".formatted(collect.getRecycler().getId())
                ));

        if (recycler.getPersonType() != PersonType.RECYCLER) {
            throw new BusinessRuleException("Recycler must be of type recycler.");
        }
                
       return repository.create(collect);
    }
}
