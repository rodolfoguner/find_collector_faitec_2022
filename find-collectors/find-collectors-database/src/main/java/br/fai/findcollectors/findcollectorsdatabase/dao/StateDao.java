package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.State;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface StateDao {

    List<State> find();

    State findById(int id);

    List<State> findStateByName(String stateName);

    State loadValues(ResultSet resultSet) throws SQLException;

}
