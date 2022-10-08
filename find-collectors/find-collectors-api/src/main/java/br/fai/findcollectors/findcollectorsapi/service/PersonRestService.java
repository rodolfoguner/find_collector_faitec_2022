package br.fai.findcollectors.findcollectorsapi.service;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;

public interface PersonRestService<T> extends BaseRestService<T> {
    Person validateLogin(Account account);

    int signUp(Account account);

}
