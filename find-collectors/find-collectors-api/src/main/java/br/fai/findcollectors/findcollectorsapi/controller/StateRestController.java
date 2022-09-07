package br.fai.findcollectors.findcollectorsapi.controller;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsapi.service.FindStatesRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/state")
@CrossOrigin(origins = "*")
public class StateRestController {

    @Autowired
    FindStatesRestService statesRestService;

    @GetMapping("")
    public ResponseEntity<List<State>> findAll() {
        List<State> states = statesRestService.find();
        return ResponseEntity.ok(states);
    }

}
