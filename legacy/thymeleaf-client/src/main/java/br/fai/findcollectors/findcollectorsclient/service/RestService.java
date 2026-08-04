package br.fai.findcollectors.findcollectorsclient.service;

import org.springframework.http.HttpHeaders;

import java.util.List;

public interface RestService<T> {

    HttpHeaders getAuthenticatedHeaders(String username, String password);

    HttpHeaders getRequestHeaders();

    List<T> get(final String resource);

    T getById(final String resource, Class<T> clazz);

    int post(final String resource, T entity);

    boolean put(final String resource, final T entity);

    boolean deleteById(final String resource);

}
