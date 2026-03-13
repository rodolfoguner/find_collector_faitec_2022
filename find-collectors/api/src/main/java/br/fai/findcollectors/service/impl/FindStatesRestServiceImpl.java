package br.fai.findcollectors.service.impl;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.service.FindStatesRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindStatesRestServiceImpl implements FindStatesRestService {

    @Autowired
    br.fai.findcollectors.repositories.StateRepository stateDao;

    @Override
    public List<State> find(String name) {

        if (name.isEmpty()) {
            return findStateByName(name);
        }

        return stateDao.find();
    }

    @Override
    public State findById(int id) {

        if (id <= 0) {
            return null;
        }

//        return stateDao.findById((long) id);
        return null;
    }

    @Override
    public List<State> findStateByName(String stateName) {

        return stateDao.findStateByName(stateName);
    }
}
