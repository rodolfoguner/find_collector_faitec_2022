package br.fai.findcollectors.service;

import br.fai.findcollectors.entities.City;

import java.util.List;

public interface FindCityRestService {

    List<City> find(String cityName, String stateName, String stateId);

    City findById(int id);

}
