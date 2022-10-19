package br.fai.findcollectors.findcollectorsapi.service;

import java.util.List;

public interface CollectRestService<T> extends BaseRestService<T> {

    boolean acceptCollect(int id, T entity);

    boolean closeCollect(int id, T entity);

    List<T> findFreeCollects(int id);

    List<T> myCollects(int id);

    List<T> acceptedCollects(int id);

}