package br.fai.findcollectors.findcollectorsclient.service.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.findcollectorsclient.service.CollectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImpl implements CollectService<Collect> {
    @Override
    public int create(Collect entity) {
        return 0;
    }

    @Override
    public List<Collect> find() {
        return null;
    }

    @Override
    public Collect findById(int id) {
        return null;
    }

    @Override
    public boolean update(int id, Collect entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
