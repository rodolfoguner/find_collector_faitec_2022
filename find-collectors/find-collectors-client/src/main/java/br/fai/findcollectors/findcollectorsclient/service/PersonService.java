package br.fai.findcollectors.findcollectorsclient.service;

import br.fai.findcollectors.entities.Person;

public interface PersonService<T> extends BaseService<T> {

    Person validateLogin(String username, String password);
    
}
