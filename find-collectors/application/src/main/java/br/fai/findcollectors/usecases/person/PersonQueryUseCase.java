package br.fai.findcollectors.usecases.person;

import br.fai.findcollectors.entities.Person;

import java.util.List;
import java.util.Optional;

public interface PersonQueryUseCase {

    List<Person> find();
    Optional<Person> findById(Long id);
    Person findPersonByEmail(String email);
    List<Person> findByGodFatherId(Long id);
}
