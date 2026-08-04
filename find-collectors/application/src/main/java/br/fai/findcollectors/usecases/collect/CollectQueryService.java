package br.fai.findcollectors.usecases.collect;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.repositories.CollectRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CollectQueryService implements CollectQueryUseCase {
    
    private final CollectRepository repository;

    @Override
    public List<Collect> find() {
        return repository.find();
    }

    @Override
    public Optional<Collect> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Collect> findPendingCollects(Long id) {
        return repository.findPendingCollects(id);
    }

    @Override
    public List<Collect> findMyCollects(Long id) {
        return repository.findMyCollects(id);
    }

    @Override
    public List<Collect> findCollectorAcceptedCollects(Long id) {
        return repository.findCollectorAcceptedCollects(id);
    }
    
}
