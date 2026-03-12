package br.fai.findcollectors.repositories;

import br.fai.findcollectors.entities.Person;

import java.util.List;

public interface PersonRepository extends BaseRepository<Person> {

    Person findPersonByEmail(String email);
    List<Person> findByGodFatherId(Long id);
}