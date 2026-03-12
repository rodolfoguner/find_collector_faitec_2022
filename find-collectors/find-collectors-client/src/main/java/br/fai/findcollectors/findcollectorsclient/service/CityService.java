package br.fai.findcollectors.findcollectorsclient.service;

import br.fai.findcollectors.entities.City;

import java.util.List;

public interface CityService {

    List<City> find();

    City findById(int id);

    List<City> findByCityName(String cityName);

    List<City> findByStateName(String stateName);

    List<City> findByStateId(int stateId);

}
