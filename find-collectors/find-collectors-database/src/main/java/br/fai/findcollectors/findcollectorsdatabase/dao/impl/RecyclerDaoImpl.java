package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.Recycler;
import br.fai.findcollectors.findcollectorsdatabase.dao.RecyclerDao;

import java.util.List;

public class RecyclerDaoImpl implements RecyclerDao<Recycler> {
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
    public boolean update(Recycler entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
