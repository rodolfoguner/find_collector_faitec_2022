package br.fai.findcollectors.findcollectorsclient.service.impl;


import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsclient.service.PersonService;
import br.fai.findcollectors.findcollectorsclient.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService<Person> {

    @Autowired
    RestService<Person> restService;


    @Override
    public int create(Person entity) {
        return 0;
    }

    @Override
    public List<Person> find() {
        return null;
    }

    @Override
    public Person findById(int id) {
        return null;
    }

    @Override
    public boolean update(int id, Person entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public Person validateLogin(String username, String password) {
        Person person = new Person();

        person.setEmail(username);
        person.setPassword(password);

        return person;
    }
}
