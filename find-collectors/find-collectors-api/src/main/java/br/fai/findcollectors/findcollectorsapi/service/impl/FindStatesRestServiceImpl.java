package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsapi.service.FindStatesRestService;
import br.fai.findcollectors.findcollectorsdatabase.dao.StateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindStatesRestServiceImpl implements FindStatesRestService {

    @Autowired
    StateDao stateDao;

    @Override
    public List<State> find() {
        return stateDao.find();
    }

    @Override
    public State findById(int id) {

        if (id <= 0) {
            return null;
        }

        return stateDao.findById(id);
    }

    @Override
    public List<State> findStateByName(String stateName) {
        
        return stateDao.findStateByName(stateName);
    }
}
