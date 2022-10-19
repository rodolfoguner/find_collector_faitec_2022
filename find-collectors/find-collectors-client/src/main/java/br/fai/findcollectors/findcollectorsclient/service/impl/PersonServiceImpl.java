package br.fai.findcollectors.findcollectorsclient.service.impl;


import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsclient.service.PersonService;
import br.fai.findcollectors.findcollectorsclient.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService<Person> {

    final String resource = "person/";

    @Autowired
    RestService<Person> restService;


    @Override
    public int create(Person entity) {

        if (entity == null) {
            return -1;
        }

        return restService.post("/signup", entity);
    }

    @Override
    public List<Person> find() {
        return null;
    }

    @Override
    public Person findById(int id) {

        if (id <= 0) {
            return null;
        }

        return restService.getById(resource + id, Person.class);
    }

    @Override
    public boolean update(int id, Person entity) {

        if (id <= 0) {
            return false;
        }

        return restService.put(resource + id, entity);
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public Person validateLogin(String username, String password) {

        Account account = new Account();

        account.setEmail(username);
        account.setPassword(password);

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Account> httpEntity = new HttpEntity<>(account);

            ResponseEntity<Person> responseEntity = restTemplate.exchange(
                    "http://localhost:8081/api/login",
                    HttpMethod.POST,
                    httpEntity,
                    Person.class
            );

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                return null;
            }

            return responseEntity.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public int godfather(Person person, Person godfather) {

        if (person == null) {
            return -1;
        }

        String[] godfatherEmail = godfather.getEmail().split("@");

        String email = godfatherEmail[0] + person.getName().toLowerCase() + "@" + godfatherEmail[1];

        person.setEmail(email.replaceAll("\\s+", ""));

        return restService.post(resource + "godfather/", person);
    }

    @Override
    public List<Person> godfatherCollectors(int id) {
        return restService.get(resource + "godfather/" + id);
    }

    @Override
    public boolean updateGodfather(int id, Person person) {

        if (id <= 0 || person == null) {
            return false;
        }

        return restService.put(resource + "godfather/" + id, person);
    }
}
