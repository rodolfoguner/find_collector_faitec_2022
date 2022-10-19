package br.fai.findcollectors.findcollectorsclient.service;

import java.util.List;

public interface CollectService<T> extends BaseService<T> {

    boolean acceptCollect(int id, T entity);

    boolean closeCollect(int id, T entity);

    List<T> findFreeCollects(int loggedPersonId);

    List<T> myCollects(int id);

}
