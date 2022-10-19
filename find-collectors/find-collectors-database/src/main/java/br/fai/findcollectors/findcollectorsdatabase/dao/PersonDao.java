package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.Person;

public interface PersonDao<T> extends BaseDao<T> {

    Person findPersonByEmail(String email);

    int godfather(Person person);

}