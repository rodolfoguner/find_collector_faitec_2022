package br.fai.findcollectors.findcollectorsapi.controller;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsapi.service.PersonRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AccountRestController {

    @Autowired
    PersonRestService<Person> personPersonRestService;

    @PostMapping("/login")
    public ResponseEntity<Person> login(@RequestBody Account account) {
        Person person = personPersonRestService.validateLogin(account);

        if (person == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(person);
    }

    @PostMapping("/signup")
    public ResponseEntity<Integer> signUp(@RequestBody Account account) {
        int id = personPersonRestService.signUp(account);

        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(id);
    }
}
