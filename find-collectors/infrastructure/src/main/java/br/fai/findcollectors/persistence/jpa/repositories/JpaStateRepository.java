package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaStateRepository extends JpaRepository<State, Long> {

    List<State> findStateByName(String stateName);
}
