package br.fai.findcollectors.findcollectorsapi.service;

import br.fai.findcollectors.entities.City;

import java.util.List;

public interface FindCityRestService {

    List<City> find();

    City findById(int id);

    List<City> findByState(String stateName);

    List<City> findCityByName(String cityName);

}
