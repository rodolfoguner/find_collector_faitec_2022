package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.Collector;
import br.fai.findcollectors.findcollectorsapi.service.CollectorRestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectorRestServiceImpl implements CollectorRestService<Collector> {
    @Override
    public List<Collector> find() {
        return null;
    }

    @Override
    public Collector findById(int id) {
        return null;
    }

    @Override
    public int create(Collector entity) {
        return 0;
    }

    @Override
    public boolean update(Collector entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
