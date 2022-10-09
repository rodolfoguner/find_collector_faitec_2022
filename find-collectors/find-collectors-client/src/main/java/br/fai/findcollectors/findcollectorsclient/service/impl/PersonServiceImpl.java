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
}
