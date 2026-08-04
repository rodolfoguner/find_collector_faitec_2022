package br.fai.findcollectors.controller;

import br.fai.findcollectors.dto.request.AuthRequest;
import br.fai.findcollectors.dto.request.CreatePersonRequest;
import br.fai.findcollectors.dto.response.PersonResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.mapper.PersonMapper;
import br.fai.findcollectors.usecases.auth.AuthenticateUserUseCase;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import jakarta.validation.Valid;
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
    public ResponseEntity<PersonResponse> login(@RequestBody @Valid AuthRequest account) {

        Person person = authenticateUserUseCase.execute(account.email(), account.password());
        if (person == null) {
            return ResponseEntity.badRequest().build();
        }

        PersonResponse response = PersonMapper.toResponse(person);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<PersonResponse> signUp(@RequestBody @Valid CreatePersonRequest personRequest) {

        Person person = PersonMapper.toEntity(personRequest);
        
        Person created = createPersonUseCase.execute(person);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }

        PersonResponse response = PersonMapper.toResponse(created);
        return ResponseEntity.ok(response);
    }
}
