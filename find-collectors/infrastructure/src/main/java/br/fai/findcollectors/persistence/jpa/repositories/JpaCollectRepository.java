package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.Collect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCollectRepository extends JpaRepository<Collect, Long> {

    List<Collect> findPendingCollects(Long id);
    List<Collect> findMyCollects(Long id);
    List<Collect> findCollectorAcceptedCollects(Long id);
}
