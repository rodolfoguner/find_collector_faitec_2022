package br.fai.findcollectors.controller;


import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.service.PersonRestService;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import br.fai.findcollectors.usecases.person.DeletePersonUseCase;
import br.fai.findcollectors.usecases.person.PersonQueryUseCase;
import br.fai.findcollectors.usecases.person.UpdatePersonUseCase;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/person")
@AllArgsConstructor
public class PersonRestController {

    private final UpdatePersonUseCase updatePersonUseCase;
    private final DeletePersonUseCase deletePersonUseCase;
    private final PersonQueryUseCase personQueryUseCase;

    @GetMapping("")
    public ResponseEntity<List<Person>> findAll() {

        return ResponseEntity.ok(personQueryUseCase.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable Long id) {

        Optional<Person> person = personQueryUseCase.findById(id);

        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(person.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable("id") Long id, @RequestBody Person person) {

        Person updated = updatePersonUseCase.execute(id, person);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        deletePersonUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}