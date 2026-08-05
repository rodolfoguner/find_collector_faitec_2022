package br.fai.findcollectors.usecases.collect;

import java.util.List;

import br.fai.findcollectors.entities.Collect;

public interface CollectQueryUseCase {
    
    List<Collect> find();Collect findById(Long id);
    List<Collect> findPendingCollects(Long id);
    List<Collect> findMyCollects(Long id);
    List<Collect> findCollectorAcceptedCollects(Long id);
}
