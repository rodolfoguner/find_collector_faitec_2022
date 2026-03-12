package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.findcollectorsapi.service.FindCityRestService;
import br.fai.findcollectors.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCityRestServiceImpl implements FindCityRestService {

    @Autowired
    CityRepository cityDao;


    @Override
    public List<City> find(String cityName, String stateName, String stateId) {

        if (!cityName.isEmpty() && stateId.isEmpty()) {
            return cityDao.findCityByName(cityName);
        }

        if (!stateName.isEmpty()) {
            return cityDao.findCitiesByStateName(stateName);
        }

        if (cityName.isEmpty() && !stateId.isEmpty()) {
            return cityDao.findByStateId(Integer.parseInt(stateId));
        }

        if (!cityName.isEmpty()) {
            return cityDao.findByStateIdAndCityName(Integer.parseInt(stateId), cityName);
        }

        return cityDao.find();
    }

    @Override
    public City findById(int id) {

        if (id <= 0) {
            return null;
        }

        return cityDao.findById(id);
    }

}
