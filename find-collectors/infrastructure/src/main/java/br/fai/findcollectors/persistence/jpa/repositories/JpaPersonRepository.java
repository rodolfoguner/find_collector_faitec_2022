package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.Person;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaPersonRepository extends JpaRepository<Person, Long> {

    @EntityGraph(attributePaths = "garbageType")
    Person findPersonByEmail(String email);

    @Override
    @EntityGraph(attributePaths = "garbageType")
    List<Person> findAll();

    @Override
    @EntityGraph(attributePaths = "garbageType")
    Optional<Person> findById(Long id);

    @Query("SELECT p FROM Person p WHERE p.godfather.id = :id ")
    List<Person> findByGodFatherId(Long id);
}
