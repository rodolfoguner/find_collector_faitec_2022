package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.Recycler;
import br.fai.findcollectors.findcollectorsapi.service.RecyclerRestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecyclerRestServiceImpl implements RecyclerRestService<Recycler> {
    @Override
    public List<Recycler> find() {
        return null;
    }

    @Override
    public Recycler findById(int id) {
        return null;
    }

    @Override
    public int create(Recycler entity) {
        return 0;
    }

    @Override
    public boolean update(int id, Recycler entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
