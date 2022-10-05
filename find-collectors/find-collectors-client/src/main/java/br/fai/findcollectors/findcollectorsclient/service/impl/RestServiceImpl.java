package br.fai.findcollectors.findcollectorsclient.service.impl;


import br.fai.findcollectors.findcollectorsclient.service.RestService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestServiceImpl<T> implements RestService<T> {

    @Override
    public HttpHeaders getAuthenticatedHeaders(String username, String password) {
        return null;
    }

    @Override
    public HttpHeaders getRequestHeaders() {
        return null;
    }

    @Override
    public List<T> get(String resource) {
        return null;
    }

    @Override
    public T getById(String resource, Class<T> clazz) {
        return null;
    }

    @Override
    public int post(String resource, T entity) {
        return 0;
    }

    @Override
    public boolean put(String resource, T entity) {
        return false;
    }

    @Override
    public boolean deleteById(String resource) {
        return false;
    }
}
