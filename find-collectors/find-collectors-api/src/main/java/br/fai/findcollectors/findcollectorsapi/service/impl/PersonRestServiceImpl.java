package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsapi.service.PersonRestService;
import br.fai.findcollectors.findcollectorsdatabase.dao.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonRestServiceImpl implements PersonRestService<Person> {

    private final String pepper = "ZiFuZC1jQGxsZWN0MHJzPw==";
    private final String salt = BCrypt.gensalt(10) + pepper;

    @Autowired
    PersonDao<Person> personDao;

    @Override
    public List<Person> find() {
        return personDao.find();
    }

    @Override
    public Person findById(int id) {
        if (id <= 0) {
            return null;
        }

        return personDao.findById(id);
    }

    @Override
    public int create(Person entity) {

        if (entity.getEmail().isEmpty() || entity.getPassword().isEmpty()) {
            return -1;
        }

        final String password = BCrypt.hashpw(entity.getPassword(), salt);

        entity.setPassword(password);

        return personDao.create(entity);
    }

    @Override
    public boolean update(int id, Person entity) {

        if (id <= 0) {
            return false;
        }

        Person person = personDao.findById(id);

        if (person == null) {
            return false;
        }

        person.setName(entity.getName());
        person.setTelephone(entity.getTelephone());
        person.setCep(entity.getCep());
        person.setAddress(entity.getAddress());
        person.setDistrict(entity.getDistrict());
        person.setNumber(entity.getNumber());
        person.setCityId(entity.getCityId());
        person.setPersonType(entity.getPersonType());
        person.setCollectPoint(entity.isCollectPoint());
        person.setGarbageType(entity.getGarbageType() != null ? entity.getGarbageType() : new ArrayList<>());
        person.setDescription(entity.getDescription());

        return personDao.update(person);
    }

    @Override
    public boolean deleteById(int id) {
        if (id <= 0) {
            return false;
        }

        return personDao.deleteById(id);
    }

    @Override
    public Person validateLogin(Account account) {
        if (account.getEmail().isEmpty() || account.getPassword().isEmpty()) {
            return null;
        }

        Person person = personDao.findPersonByEmail(account.getEmail());

        if (person == null) {
            return null;
        }

        if (!BCrypt.checkpw(account.getPassword(), person.getPassword())) {
            return null;
        }

        person.setPassword(null);

        return person;
    }

    @Override
    public boolean changePassword(Account account) {

        if (account.getEmail().isEmpty() || account.getPassword().isEmpty()) {
            return false;
        }

        Person person = personDao.findPersonByEmail(account.getEmail());

        if (person == null) return false;

        final String password = BCrypt.hashpw(account.getPassword(), salt);

        person.setPassword(password);

        return personDao.changePassword(person);
    }

}