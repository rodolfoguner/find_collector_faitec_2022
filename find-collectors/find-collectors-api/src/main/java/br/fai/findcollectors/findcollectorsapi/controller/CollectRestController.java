package br.fai.findcollectors.findcollectorsapi.controller;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsapi.service.CollectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collect")
@CrossOrigin(origins = "*")
public class CollectRestController {

    @Autowired
    CollectRestService<Collect> collectRestService;

    @GetMapping("")
    public ResponseEntity<List<Collect>> findAll() {
        return ResponseEntity.ok(collectRestService.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collect> findById(@PathVariable int id) {

        Collect collect = collectRestService.findById(id);

        if (collect == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(collect);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> update(@PathVariable("id") int id, @RequestBody Collect collect) {
        boolean updated = collectRestService.update(id, collect);

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable int id) {
        boolean deleted = collectRestService.deleteById(id);

        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }
}
