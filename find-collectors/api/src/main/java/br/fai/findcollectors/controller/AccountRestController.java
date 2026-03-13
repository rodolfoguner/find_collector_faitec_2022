package br.fai.findcollectors.controller;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.service.PersonRestService;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AccountRestController {

    @Autowired
    PersonRestService<Person> personPersonRestService;

    private final CreatePersonUseCase createPersonUseCase;

    @PostMapping("/login")
    public ResponseEntity<Person> login(@RequestBody Account account) {
        Person person = personPersonRestService.validateLogin(account);

        if (person == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(person);
    }

    @PostMapping("/signup")
    public ResponseEntity<Person> signUp(@RequestBody Person person) {
        Person saved = createPersonUseCase.execute(person);

        return ResponseEntity.ok(saved);
    }
}
