package br.fai.findcollectors.usecases.collect;

import java.util.List;

import org.springframework.stereotype.Service;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
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
    public Collect findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                    ErrorCode.COLLECT_NOT_FOUND,
                    "Collect with id %d not found".formatted(id)
                ));
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
