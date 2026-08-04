package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaPersonRepository extends JpaRepository<Person, Long> {

    Person findPersonByEmail(String email);
    @Query("SELECT p FROM Person p WHERE p.godfather.id = :id ")
    List<Person> findByGodFatherId(Long id);
}
