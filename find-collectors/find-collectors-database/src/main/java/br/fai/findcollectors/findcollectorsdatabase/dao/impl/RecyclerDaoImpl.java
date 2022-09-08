package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.Recycler;
import br.fai.findcollectors.findcollectorsdatabase.dao.RecyclerDao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
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

    @Override
    public Recycler loadValues(ResultSet resultSet) throws SQLException {
        return null;
    }
}
