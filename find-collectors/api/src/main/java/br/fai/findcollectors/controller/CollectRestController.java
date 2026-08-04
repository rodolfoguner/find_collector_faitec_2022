package br.fai.findcollectors.controller;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.usecases.collect.CollectQueryUseCase;
import br.fai.findcollectors.usecases.collect.CreateCollectUseCase;
import br.fai.findcollectors.usecases.collect.DeleteCollectUseCase;
import br.fai.findcollectors.usecases.collect.UpdateCollectUseCase;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collect")
@AllArgsConstructor
public class CollectRestController {

    private final CollectQueryUseCase queryUseCase;
    private final CreateCollectUseCase createCollectUseCase;
    private final UpdateCollectUseCase updateCollectUseCase;
    private final DeleteCollectUseCase deleteCollectUseCase;

    @PostMapping("")
    public ResponseEntity<Collect> create(@RequestBody Collect collect) {
        
        Collect created = createCollectUseCase.execute(collect);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(created);
    }

    @GetMapping("")
    public ResponseEntity<List<Collect>> findAll() {
        return ResponseEntity.ok(queryUseCase.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collect> findById(@PathVariable Long id) {

        Optional<Collect> collect = queryUseCase.findById(id);

        if (collect.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(collect.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Collect> update(@PathVariable Long id, @RequestBody Collect collect) {
        Collect updated = updateCollectUseCase.execute(id, collect);

        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        
        deleteCollectUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    // @PutMapping("/accept-collect/{id}")
    // public ResponseEntity<Boolean> acceptCollect(@PathVariable("id") int id, @RequestBody Collect collect) {
    //     boolean updated = collectRestService.acceptCollect(id, collect);

    //     if (!updated) {
    //         return ResponseEntity.badRequest().build();
    //     }

    //     return ResponseEntity.ok(true);
    // }

    // @PutMapping("/close-collect/{id}")
    // public ResponseEntity<Boolean> closeCollect(@PathVariable("id") int id, @RequestBody Collect collect) {
    //     boolean updated = collectRestService.closeCollect(id, collect);

    //     if (!updated) {
    //         return ResponseEntity.badRequest().build();
    //     }

    //     return ResponseEntity.ok(true);
    // }

    @GetMapping("/free-collects/{id}")
    public ResponseEntity<List<Collect>> freeCollects(@PathVariable Long id) {

        return ResponseEntity.ok(queryUseCase.findPendingCollects(id));

    }

    @GetMapping("/my-collects/{id}")
    public ResponseEntity<List<Collect>> myCollects(@PathVariable Long id) {

        return ResponseEntity.ok(queryUseCase.findMyCollects(id));

    }

    @GetMapping("/accepted-collects/{id}")
    public ResponseEntity<List<Collect>> acceptedCollects(@PathVariable Long id) {
        
        return ResponseEntity.ok(queryUseCase.findCollectorAcceptedCollects(id));
    }
}