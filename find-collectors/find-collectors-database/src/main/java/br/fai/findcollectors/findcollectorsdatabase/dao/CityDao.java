package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.City;

import java.util.List;

public interface CityDao {

    List<City> find();

    City findById(int id);

    List<City> findByState(String stateName);

    List<City> findCityByName(String cityName);

}
