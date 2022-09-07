package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.findcollectorsdatabase.dao.CollectDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CollectDaoImpl implements CollectDao<Collect> {
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
