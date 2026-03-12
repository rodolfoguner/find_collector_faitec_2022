package br.fai.findcollectors.findcollectorsapi.service;

import java.util.List;

public interface BaseRestService<T> {

    List<T> find();

    T findById(int id);

    int create(T entity);

    boolean update(int id, T entity);

    boolean deleteById(int id);
}
