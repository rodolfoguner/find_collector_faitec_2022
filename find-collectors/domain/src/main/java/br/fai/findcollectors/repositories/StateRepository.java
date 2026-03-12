package br.fai.findcollectors.repositories;

import br.fai.findcollectors.entities.State;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StateRepository {

    List<State> find();
    Optional<State> findById(Long id);
    List<State> findStateByName(String stateName);
}
