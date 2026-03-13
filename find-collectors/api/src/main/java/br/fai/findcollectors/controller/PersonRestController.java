package br.fai.findcollectors.controller;


import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.service.PersonRestService;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import lombok.AllArgsConstructor;
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

    @PostMapping("/godfather")
    public ResponseEntity<Integer> godfather(@RequestBody Person person) {
        int id = personRestService.godfather(person);

        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(id);
    }

    @GetMapping("/godfather/{id}")
    public ResponseEntity<List<Person>> getGodfatherCollectors(@PathVariable("id") final int id) {
        return ResponseEntity.ok(personRestService.godfatherCollectors(id));
    }

    @PutMapping("/godfather/{id}")
    public ResponseEntity<Boolean> godfather(@PathVariable("id") final int id, @RequestBody Person person) {
        boolean updated = personRestService.updateGodfather(id, person);

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/collect-points")
    public ResponseEntity<List<Person>> getCollectPoints() {
        return ResponseEntity.ok(personRestService.getCollectPoints());
    }
}