package br.fai.findcollectors.repositories;

import java.util.List;

public interface CollectRepository<T> extends BaseRepository<T> {

    boolean acceptCollect(T entity);

    boolean closeCollect(T entity);

    List<T> findFreeCollects(int id);

    List<T> MyCollects(int id);

    List<T> acceptedCollects(int id);

}