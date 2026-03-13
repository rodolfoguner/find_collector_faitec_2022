package br.fai.findcollectors.persistence.jpa.repositories;

import br.fai.findcollectors.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCityRepository extends JpaRepository<City, Long> {

    List<City> findCityByName(String cityName);
    List<City> findCitiesByStateName(String stateName);
    List<City> findByStateId(Long id);
    List<City> findCityByNameAndStateId(String cityName, Long stateId);
}
