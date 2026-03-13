package br.fai.findcollectors.controller;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.service.FindCityRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
public class CityRestController {

    @Autowired
    FindCityRestService cityRestService;

    @GetMapping("")
    public ResponseEntity<List<City>> findaAllCities(@RequestParam(defaultValue = "") String cityName,
                                                     @RequestParam(defaultValue = "") String stateName,
                                                     @RequestParam(defaultValue = "") String stateId) {
        return ResponseEntity.ok(cityRestService.find(cityName, stateName, stateId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> findCityById(@PathVariable int id) {
        City city = cityRestService.findById(id);

        if (city == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(city);

    }

}
