package br.fai.findcollectors.findcollectorsapi.controller;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsapi.service.FindStatesRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/state")
@CrossOrigin(origins = "*")
public class StateRestController {

    @Autowired
    FindStatesRestService statesRestService;

    @GetMapping("")
    public ResponseEntity<List<State>> findAll(@RequestParam(defaultValue = "") String name) {
        List<State> states = new ArrayList<>();

        if (!name.isEmpty()) {
            states = statesRestService.findStateByName(name);
            return ResponseEntity.ok(states);
        }

        states = statesRestService.find();
        return ResponseEntity.ok(states);
    }

    @GetMapping("/{id}")
    public ResponseEntity<State> findById(@PathVariable int id) {

        State state = statesRestService.findById(id);

        if (state == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(state);
    }

}
