package br.fai.findcollectors.service;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;

import java.util.List;

public interface PersonRestService<T> extends BaseRestService<T> {
    Person validateLogin(Account account);

    int godfather(Person person);

    List<Person> godfatherCollectors(int id);

    boolean updateGodfather(int id, Person entity);

    List<Person> getCollectPoints();

}