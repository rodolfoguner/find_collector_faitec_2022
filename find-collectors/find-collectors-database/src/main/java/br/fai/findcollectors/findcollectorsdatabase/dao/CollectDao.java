package br.fai.findcollectors.findcollectorsdatabase.dao;

import java.util.List;

public interface CollectDao<T> extends BaseDao<T> {

    boolean acceptCollect(T entity);

    boolean closeCollect(T entity);

    List<T> findFreeCollects();
}