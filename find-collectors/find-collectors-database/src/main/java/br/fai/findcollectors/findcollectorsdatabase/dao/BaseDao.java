package br.fai.findcollectors.findcollectorsdatabase.dao;

import java.util.List;

public interface BaseDao<T> {

    List<T> find();

    T findById(int id);

    int create(T entity);

    boolean update(T entity);

    boolean deleteById(int id);
}
