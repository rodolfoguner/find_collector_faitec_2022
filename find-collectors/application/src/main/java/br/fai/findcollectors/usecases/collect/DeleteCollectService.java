package br.fai.findcollectors.usecases.collect;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.CollectRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeleteCollectService implements DeleteCollectUseCase {
    
    private final CollectRepository repository;

    @Override
    public void execute(Long id) {
        
        Collect existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                    ErrorCode.COLLECT_NOT_FOUND,
                    "Collect with id %d not found".formatted(id)
                ));
        
        repository.deleteById(existing.getId());
    }
}
