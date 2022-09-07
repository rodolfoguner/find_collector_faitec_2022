package br.fai.findcollectors.findcollectorsapi.controller;

import br.fai.findcollectors.entities.County;
import br.fai.findcollectors.findcollectorsapi.service.FindCountyRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/county")
@CrossOrigin(origins = "*")
public class CountyRestController {

    @Autowired
    FindCountyRestService countyRestService;

    @GetMapping("")
    public ResponseEntity<List<County>> findaAllCounties() {
        return ResponseEntity.ok(countyRestService.find());
    }

}
