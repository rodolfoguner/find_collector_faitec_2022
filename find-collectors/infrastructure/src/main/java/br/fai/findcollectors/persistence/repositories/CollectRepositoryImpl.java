package br.fai.findcollectors.persistence.repositories;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.persistence.jpa.repositories.JpaCollectRepository;
import br.fai.findcollectors.repositories.CollectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CollectRepositoryImpl implements CollectRepository {

    private JpaCollectRepository repository;

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

    @Override
    public List<Collect> find() {
        return repository.findAll();
    }

    @Override
    public Optional<Collect> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Collect create(Collect entity) {
        return repository.save(entity);
    }

    @Override
    public Collect update(Collect entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}