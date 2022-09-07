package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.State;

import java.util.List;

public interface StateDao {

    List<State> find();

    State findById();

}
