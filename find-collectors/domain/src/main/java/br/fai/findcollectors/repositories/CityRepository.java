package br.fai.findcollectors.repositories;

import br.fai.findcollectors.entities.City;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CityRepository {

    List<City> find();

    City findById(int id);

    List<City> findCityByName(String cityName);

    List<City> findCitiesByStateName(String stateName);

    List<City> findByStateId(int id);

    List<City> findByStateIdAndCityName(int stateId, String cityName);

    City loadValues(ResultSet resultSet) throws SQLException;

}
