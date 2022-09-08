package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.City;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CityDao {

    List<City> find();

    City findById(int id);

    List<City> findCityByName(String cityName);

    List<City> findByStateName(String stateName);

    List<City> findByStateId(int id);

    List<City> findByStateIdAndCityName(int stateId, String cityName);

    City loadValues(ResultSet resultSet) throws SQLException;

}
