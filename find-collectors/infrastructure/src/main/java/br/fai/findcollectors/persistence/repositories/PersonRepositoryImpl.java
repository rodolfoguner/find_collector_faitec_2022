package br.fai.findcollectors.persistence.repositories;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.persistence.jpa.repositories.JpaPersonRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private JpaPersonRepository repository;

    @Override
    public Person findPersonByEmail(String email) {
        return repository.findPersonByEmail(email);
    }

    @Override
    public List<Person> findByGodFatherId(Long id) {
        return repository.findByGodFatherId(id);
    }

    @Override
    public List<Person> find() {
        return repository.findAll();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Person create(Person entity) {
        return repository.save(entity);
    }

    @Override
    public Person update(Person entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}