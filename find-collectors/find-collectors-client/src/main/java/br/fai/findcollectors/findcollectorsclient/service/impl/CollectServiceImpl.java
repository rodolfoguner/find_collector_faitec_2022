package br.fai.findcollectors.findcollectorsclient.service.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.findcollectorsclient.service.CollectService;
import br.fai.findcollectors.findcollectorsclient.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImpl implements CollectService<Collect> {

    final String resource = "collect/";

    @Autowired
    RestService<Collect> restService;

    @Override
    public int create(Collect entity) {

        if (entity == null) {
            return -1;
        }

        return restService.post(resource, entity);
    }

    @Override
    public List<Collect> find() {
        return restService.get(resource);
    }

    @Override
    public Collect findById(int id) {
        if (id <= 0) {
            return null;
        }

        return restService.getById(resource + id, Collect.class);
    }

    @Override
    public boolean update(int id, Collect entity) {

        if (id <= 0 || entity == null) {
            return false;
        }

        return restService.put(resource + id, entity);
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
