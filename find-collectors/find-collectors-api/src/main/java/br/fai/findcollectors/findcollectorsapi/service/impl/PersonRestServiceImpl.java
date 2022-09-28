package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsapi.service.PersonRestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonRestServiceImpl implements PersonRestService<Person> {
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

    @Override
    public Person validateLogin(Account account) {
        return null;
    }

    @Override
    public int signUp(Account account) {

        if (account.getEmail().isEmpty() || account.getPassword().isEmpty()) {
            return -1;
        }

        Person person = new Person();

        person.setEmail(account.getEmail());
        person.setPassword(account.getPassword());

        return this.create(person);
    }
}
