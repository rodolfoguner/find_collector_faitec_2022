package br.fai.findcollectors.findcollectorsclient.service;

import br.fai.findcollectors.entities.Person;

import java.util.List;

public interface PersonService<T> extends BaseService<T> {

    Person validateLogin(String username, String password);

    int godfather(Person person);

    List<Person> godfatherCollectors(int id);

}
