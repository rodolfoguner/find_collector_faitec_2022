package br.fai.findcollectors.findcollectorsclient.service.impl;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsclient.service.RestService;
import br.fai.findcollectors.findcollectorsclient.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    @Autowired
    RestService<State> restService;

    @Override
    public List<State> find() {
        return restService.get("/state");
    }

    @Override
    public State findById(int id) {
        return null;
    }

    @Override
    public List<State> findStateByName(String stateName) {
        return null;
    }
}
