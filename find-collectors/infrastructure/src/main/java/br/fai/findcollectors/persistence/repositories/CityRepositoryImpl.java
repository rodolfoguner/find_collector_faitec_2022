package br.fai.findcollectors.persistence.repositories;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.persistence.jpa.repositories.JpaCityRepository;
import br.fai.findcollectors.repositories.CityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CityRepositoryImpl implements CityRepository {

    private final JpaCityRepository repository;

    @Override
    public List<City> find() {
        return repository.findAll();
    }

    @Override
    public Optional<City> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<City> findCityByName(String cityName) {
        return repository.findCityByName(cityName);
    }

    @Override
    public List<City> findCitiesByStateName(String stateName) {
        return repository.findCitiesByStateName(stateName);
    }

    @Override
    public List<City> findByStateId(Long id) {
        return repository.findByStateId(id);
    }

    @Override
    public List<City> findByStateIdAndCityName(Long stateId, String cityName) {
        return repository.findByStateIdAndCityName(stateId, cityName);
    }

}
