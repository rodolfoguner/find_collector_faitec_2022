package br.fai.findcollectors.repositories;

import br.fai.findcollectors.entities.Collect;

import java.util.List;

public interface CollectRepository extends BaseRepository<Collect> {

    List<Collect> findPendingCollects(Long id);
    List<Collect> findMyCollects(Long id);
    List<Collect> findCollectorAcceptedCollects(Long id);
}