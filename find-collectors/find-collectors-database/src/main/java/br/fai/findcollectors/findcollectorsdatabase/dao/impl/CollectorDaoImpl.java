package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.Collector;
import br.fai.findcollectors.findcollectorsdatabase.dao.CollectorDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CollectorDaoImpl implements CollectorDao<Collector> {
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
