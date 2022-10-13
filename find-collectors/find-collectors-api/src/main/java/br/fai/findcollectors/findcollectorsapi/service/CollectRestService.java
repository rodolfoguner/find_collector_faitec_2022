package br.fai.findcollectors.findcollectorsapi.service;

public interface CollectRestService<T> extends BaseRestService<T> {

    boolean acceptCollect(int id, T entity);

    boolean closeCollect(int id, T entity);

}
