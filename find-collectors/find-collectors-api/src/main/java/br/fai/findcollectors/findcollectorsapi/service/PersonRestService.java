package br.fai.findcollectors.findcollectorsapi.service;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;

import java.util.List;

public interface PersonRestService<T> extends BaseRestService<T> {
    Person validateLogin(Account account);

    int godfather(Person person);

    List<Person> godfatherCollectors(int id);

}