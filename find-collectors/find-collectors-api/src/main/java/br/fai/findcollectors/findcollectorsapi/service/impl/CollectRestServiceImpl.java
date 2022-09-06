package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.findcollectorsapi.service.CollectRestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectRestServiceImpl implements CollectRestService<Collect> {
    @Override
    public List<Collect> find() {
        return null;
    }

    @Override
    public Collect findById(int id) {
        return null;
    }

    @Override
    public int create(Collect entity) {
        return 0;
    }

    @Override
    public boolean update(Collect entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
