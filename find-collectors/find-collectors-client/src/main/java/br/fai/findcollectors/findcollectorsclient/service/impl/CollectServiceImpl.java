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

        if (id <= 0) {
            return false;
        }

        return restService.deleteById(resource + id);
    }

    @Override
    public boolean acceptCollect(int id, Collect entity) {

        if (id <= 0 || entity == null || entity.getCollectorId() <= 0) {
            return false;
        }

        entity.setAccept(true);

        return restService.put(resource + "accept-collect/" + id, entity);
    }

    @Override
    public boolean closeCollect(int id, Collect entity) {

        if (id <= 0) {
            return false;
        }

        entity.setCollected(true);

        return restService.put(resource + "close-collect/" + id, entity);
    }

    @Override
    public List<Collect> findFreeCollects(int loggedPersonId) {
        return restService.get(resource + "free-collects/" + loggedPersonId);
    }

    @Override
    public List<Collect> myCollects(int id) {

        if (id <= 0) {
            return null;
        }

        return restService.get(resource + "my-collects/" + id);
    }

    @Override
    public List<Collect> acceptedCollects(int id) {

        if (id <= 0) {
            return null;
        }

        return restService.get(resource + "accepted-collects/" + id);
    }
}
