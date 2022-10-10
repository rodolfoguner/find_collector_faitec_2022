package br.fai.findcollectors.findcollectorsapi.controller;


import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsapi.service.PersonRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
@CrossOrigin(origins = "*")
public class PersonRestController {

    @Autowired
    PersonRestService<Person> personRestService;

    @GetMapping("")
    public ResponseEntity<List<Person>> findAll() {
        return ResponseEntity.ok(personRestService.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {

        Person person = personRestService.findById(id);

        if (person == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(person);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> update(@PathVariable("id") int id, @RequestBody Person person) {
        boolean updated = personRestService.update(id, person);

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable int id) {
        boolean deleted = personRestService.deleteById(id);

        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }
}
