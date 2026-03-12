package br.fai.findcollectors.repositories;

import br.fai.findcollectors.entities.Person;

import java.util.List;

public interface PersonRepository<T> extends BaseRepository<T> {

    Person findPersonByEmail(String email);

    int godfather(Person person);

    List<Person> godfatherCollectors(int id);

    boolean updateGodfather(int id, Person person);

    List<Person> getCollectPoints();

}