package br.fai.findcollectors.persistence.repositories;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.persistence.jpa.repositories.JpaStateRepository;
import br.fai.findcollectors.repositories.StateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class StateRepositoryImpl implements StateRepository {

    private JpaStateRepository repository;

    @Override
    public List<State> find() {
        return repository.findAll();
    }

    @Override
    public Optional<State> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<State> findStateByName(String stateName) {
        return repository.findStateByName(stateName);
    }
}
