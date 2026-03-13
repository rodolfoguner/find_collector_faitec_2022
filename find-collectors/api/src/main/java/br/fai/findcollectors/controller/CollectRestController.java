package br.fai.findcollectors.controller;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.service.CollectRestService;
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

    @PostMapping("")
    public ResponseEntity<Integer> create(@RequestBody Collect collect) {
        int id = collectRestService.create(collect);

        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(id);
    }

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

    @PutMapping("/accept-collect/{id}")
    public ResponseEntity<Boolean> acceptCollect(@PathVariable("id") int id, @RequestBody Collect collect) {
        boolean updated = collectRestService.acceptCollect(id, collect);

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }

    @PutMapping("/close-collect/{id}")
    public ResponseEntity<Boolean> closeCollect(@PathVariable("id") int id, @RequestBody Collect collect) {
        boolean updated = collectRestService.closeCollect(id, collect);

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/free-collects/{id}")
    public ResponseEntity<List<Collect>> freeCollects(@PathVariable("id") final int id) {

        return ResponseEntity.ok(collectRestService.findFreeCollects(id));

    }

    @GetMapping("/my-collects/{id}")
    public ResponseEntity<List<Collect>> myCollects(@PathVariable("id") int id) {

        return ResponseEntity.ok(collectRestService.myCollects(id));

    }

    @GetMapping("/accepted-collects/{id}")
    public ResponseEntity<List<Collect>> acceptedCollects(@PathVariable("id") final int id) {
        return ResponseEntity.ok(collectRestService.acceptedCollects(id));
    }
}