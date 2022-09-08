package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.findcollectorsapi.service.FindCityRestService;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCityRestServiceImpl implements FindCityRestService {

    @Autowired
    CityDao cityDao;


    @Override
    public List<City> find(String cityName, String stateName, int stateId) {
        return null;
    }

    @Override
    public City findById(int id) {
        return null;
    }
    
}
