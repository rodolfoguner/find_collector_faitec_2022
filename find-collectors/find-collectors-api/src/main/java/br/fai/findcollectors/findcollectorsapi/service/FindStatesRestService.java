package br.fai.findcollectors.findcollectorsapi.service;

import br.fai.findcollectors.entities.State;

import java.util.List;

public interface FindStatesRestService {

    List<State> find(String name);

    State findById(int id);

    List<State> findStateByName(String stateName);

}
