package br.fai.findcollectors.findcollectorsdatabase.dao;

public interface CollectDao<T> extends BaseDao<T> {

    boolean acceptCollect(T entity);

    boolean closeCollect(T entity);
}
