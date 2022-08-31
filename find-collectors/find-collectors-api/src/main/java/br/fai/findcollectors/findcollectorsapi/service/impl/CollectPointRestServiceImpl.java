package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.CollectPoint;
import br.fai.findcollectors.findcollectorsapi.service.CollectPointRestService;

import java.util.List;

public class CollectPointRestServiceImpl implements CollectPointRestService<CollectPoint> {
    @Override
    public List<CollectPoint> find() {
        return null;
    }

    @Override
    public CollectPoint findById(int id) {
        return null;
    }

    @Override
    public int create(CollectPoint entity) {
        return 0;
    }

    @Override
    public boolean update(CollectPoint entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
