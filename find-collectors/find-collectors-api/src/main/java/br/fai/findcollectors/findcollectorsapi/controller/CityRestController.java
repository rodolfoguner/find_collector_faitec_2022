package br.fai.findcollectors.findcollectorsapi.controller;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.findcollectorsapi.service.FindCityRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@CrossOrigin(origins = "*")
public class CityRestController {

    @Autowired
    FindCityRestService cityRestService;

    @GetMapping("")
    public ResponseEntity<List<City>> findaAllCities() {
        return ResponseEntity.ok(cityRestService.find());
    }

}
