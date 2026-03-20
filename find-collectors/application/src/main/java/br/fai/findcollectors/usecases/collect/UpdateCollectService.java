package br.fai.findcollectors.usecases.collect;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.repositories.CollectRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UpdateCollectService implements UpdateCollectUseCase {
    
    private final CollectRepository repository;

    @Override
    public Collect execute(Long id, Collect collect) {
        Optional<Collect> existingDb = repository.findById(id);
        if (existingDb.isEmpty()) {
            throw new RuntimeException();
        }
        
        Collect existing = existingDb.get();
        
        existing.setDateAndTime(collect.getDateAndTime());
        existing.setGarbageType(collect.getGarbageType());
        existing.setRecurrent(collect.isRecurrent());
        existing.setAddress(collect.getAddress());
        
        return repository.update(existing);
    }
}
