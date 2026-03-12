package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPersonRepository extends JpaRepository<Person, Long> {

    Person findPersonByEmail(String email);
    List<Person> findByGodFatherId(Long id);
}
