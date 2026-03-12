package br.fai.findcollectors.repositories;

import br.fai.findcollectors.entities.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository {

    List<City> find();
    Optional<City> findById(Long id);
    List<City> findCityByName(String cityName);
    List<City> findCitiesByStateName(String stateName);
    List<City> findByStateId(Long id);
    List<City> findByStateIdAndCityName(Long stateId, String cityName);
}
