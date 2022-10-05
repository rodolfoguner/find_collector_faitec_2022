package br.fai.findcollectors.findcollectorsclient.service;

import java.util.List;

public interface BaseService<T> {

    int create(T entity);

    List<T> find();

    T findById(int id);

    boolean update(int id, T entity);

    boolean deleteById(int id);
    
}
