package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.Collect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaCollectRepository extends JpaRepository<Collect, Long> {

    @Query("SELECT c FROM Collect c WHERE c.accept IS FALSE AND c.collector.id != :id")
    List<Collect> findPendingCollects(Long id);
    @Query("SELECT c FROM Collect c WHERE c.collector.id = :id")
    List<Collect> findMyCollects(Long id);
    @Query("SELECT c FROM Collect c WHERE c.accept IS TRUE AND c.collector.id = :id")
    List<Collect> findCollectorAcceptedCollects(Long id);
}
