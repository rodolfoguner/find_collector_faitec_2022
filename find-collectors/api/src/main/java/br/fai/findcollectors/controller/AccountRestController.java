package br.fai.findcollectors.controller;

import br.fai.findcollectors.entities.Account;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.usecases.auth.AuthenticateUserUseCase;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AccountRestController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final CreatePersonUseCase createPersonUseCase;

    @PostMapping("/login")
    public ResponseEntity<Person> login(@RequestBody Account account) {

        Person person = authenticateUserUseCase.execute(account.getEmail(), account.getPassword());
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
