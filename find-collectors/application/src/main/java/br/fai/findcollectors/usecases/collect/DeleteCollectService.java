package br.fai.findcollectors.usecases.collect;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.repositories.CollectRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeleteCollectService implements DeleteCollectUseCase {
    
    private final CollectRepository repository;

    @Override
    public void execute(Long id) {
        
        Optional<Collect> exiting = repository.findById(id);
        if (exiting.isEmpty()) {
            throw new RuntimeException();
        }
        
        repository.deleteById(id);
    }
}
