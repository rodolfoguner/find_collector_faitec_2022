package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.enums.CollectStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaCollectRepository extends JpaRepository<Collect, Long> {

    @Override
    @EntityGraph(attributePaths = {"garbageType", "collector", "recycler"})
    List<Collect> findAll();

    @Override
    @EntityGraph(attributePaths = {"garbageType", "collector", "recycler"})
    Optional<Collect> findById(Long id);

    @EntityGraph(attributePaths = {"garbageType", "collector", "recycler"})
    @Query("SELECT DISTINCT c FROM Collect c "
            + "WHERE c.status = :status "
            + "AND c.recycler.id != :id")
    List<Collect> findPendingCollects(Long id, CollectStatus status);

    @EntityGraph(attributePaths = {"garbageType", "collector", "recycler"})
    @Query("SELECT DISTINCT c FROM Collect c "
            + "WHERE c.recycler.id = :id "
            + "AND c.status != :excludedStatus")
    List<Collect> findMyCollects(Long id, CollectStatus excludedStatus);

    @EntityGraph(attributePaths = {"garbageType", "collector", "recycler"})
    @Query("SELECT DISTINCT c FROM Collect c "
            + "WHERE c.status = :status "
            + "AND c.collector.id = :id")
    List<Collect> findCollectorAcceptedCollects(Long id, CollectStatus status);
}
