package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.Person;

import java.util.List;

public interface PersonDao<T> extends BaseDao<T> {

    Person findPersonByEmail(String email);

    int godfather(Person person);

    List<Person> godfatherCollectors(int id);

    boolean updateGodfather(int id, Person person);

    List<Person> getCollectPoints();

}