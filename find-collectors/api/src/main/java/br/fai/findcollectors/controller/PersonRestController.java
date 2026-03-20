package br.fai.findcollectors.controller;


import br.fai.findcollectors.dto.response.PersonResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.mapper.PersonMapper;
import br.fai.findcollectors.usecases.person.DeletePersonUseCase;
import br.fai.findcollectors.usecases.person.PersonQueryUseCase;
import br.fai.findcollectors.usecases.person.UpdatePersonUseCase;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<List<PersonResponse>> findAll() {

        List<Person> persons = personQueryUseCase.find();
        List<PersonResponse> response = persons
                .stream()
                .map(PersonMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable final Long id) {

        Optional<Person> person = personQueryUseCase.findById(id);

        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PersonResponse response = PersonMapper.toResponse(person.get());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable final Long id, @RequestBody Person person) {

        Person updated = updatePersonUseCase.execute(id, person);

        PersonResponse response = PersonMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {

        deletePersonUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}