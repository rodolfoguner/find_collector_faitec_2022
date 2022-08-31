package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsdatabase.dao.PersonDao;

import java.util.List;

public class PersonDaoImpl implements PersonDao<Person> {
    @Override
    public List<Person> find() {
        return null;
    }

    @Override
    public Person findById(int id) {
        return null;
    }

    @Override
    public int create(Person entity) {
        return 0;
    }

    @Override
    public boolean update(Person entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
