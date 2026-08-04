package br.fai.findcollectors.findcollectorsclient.service;

import br.fai.findcollectors.entities.State;

import java.util.List;

public interface StateService {

    List<State> find();

    State findById(int id);

    List<State> findStateByName(String stateName);

}
