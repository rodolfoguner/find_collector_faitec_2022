package br.fai.findcollectors.findcollectorsapi.service;

import br.fai.findcollectors.entities.State;

import java.util.List;

public interface FindStatesRestService {

    List<State> find();

    State findById(int id);

    List<State> findStateByName(String stateName);

}
