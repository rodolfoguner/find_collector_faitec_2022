package br.fai.findcollectors.usecases.collect;

import java.util.List;
import java.util.Optional;

import br.fai.findcollectors.entities.Collect;

public interface CollectQueryUseCase {
    
    List<Collect> find();
    Optional<Collect> findById(Long id);
    List<Collect> findPendingCollects(Long id);
    List<Collect> findMyCollects(Long id);
    List<Collect> findCollectorAcceptedCollects(Long id);
}
