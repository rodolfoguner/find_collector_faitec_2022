package br.fai.findcollectors.controller;

import br.fai.findcollectors.dto.request.CreateCollectRequest;
import br.fai.findcollectors.dto.request.UpdateCollectRequest;
import br.fai.findcollectors.dto.response.CollectResponse;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.mapper.CollectMapper;
import br.fai.findcollectors.usecases.collect.CollectQueryUseCase;
import br.fai.findcollectors.usecases.collect.CreateCollectUseCase;
import br.fai.findcollectors.usecases.collect.DeleteCollectUseCase;
import br.fai.findcollectors.usecases.collect.UpdateCollectUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collect")
@AllArgsConstructor
public class CollectRestController {

    private final CollectQueryUseCase queryUseCase;
    private final CreateCollectUseCase createCollectUseCase;
    private final UpdateCollectUseCase updateCollectUseCase;
    private final DeleteCollectUseCase deleteCollectUseCase;

    @PostMapping("")
    public ResponseEntity<CollectResponse> create(@RequestBody @Valid CreateCollectRequest request) {

        Collect created = createCollectUseCase.execute(CollectMapper.toEntity(request));

        return ResponseEntity.ok(CollectMapper.toResponse(created));
    }

    @GetMapping("")
    public ResponseEntity<List<CollectResponse>> findAll() {
        return ResponseEntity.ok(toResponse(queryUseCase.find()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectResponse> findById(@PathVariable Long id) {

        Collect collect = queryUseCase.findById(id);

        return ResponseEntity.ok(CollectMapper.toResponse(collect));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollectResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCollectRequest request
    ) {

        Collect updated = updateCollectUseCase.execute(id, CollectMapper.toEntity(request));

        return ResponseEntity.ok(CollectMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        
        deleteCollectUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/free-collects/{id}")
    public ResponseEntity<List<CollectResponse>> freeCollects(@PathVariable Long id) {

        return ResponseEntity.ok(toResponse(queryUseCase.findPendingCollects(id)));

    }

    @GetMapping("/my-collects/{id}")
    public ResponseEntity<List<CollectResponse>> myCollects(@PathVariable Long id) {

        return ResponseEntity.ok(toResponse(queryUseCase.findMyCollects(id)));

    }

    @GetMapping("/accepted-collects/{id}")
    public ResponseEntity<List<CollectResponse>> acceptedCollects(@PathVariable Long id) {

        return ResponseEntity.ok(toResponse(queryUseCase.findCollectorAcceptedCollects(id)));
    }

    private List<CollectResponse> toResponse(List<Collect> collects) {
        return collects.stream()
                .map(CollectMapper::toResponse)
                .toList();
    }
}
