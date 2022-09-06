package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.County;
import br.fai.findcollectors.findcollectorsdatabase.dao.CountyDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CountyDaoImpl implements CountyDao {
    @Override
    public List<County> find() {
        return null;
    }

    @Override
    public County findById() {
        return null;
    }

    @Override
    public List<County> findByState() {
        return null;
    }
}
