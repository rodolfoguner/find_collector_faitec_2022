package br.fai.findcollectors.findcollectorsclient.service.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.findcollectorsclient.service.CityService;
import br.fai.findcollectors.findcollectorsclient.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    final String resource = "city";

    @Autowired
    RestService<City> restService;

    @Override
    public List<City> find() {
        return null;
    }

    @Override
    public City findById(int id) {
        return null;
    }

    @Override
    public List<City> findByCityName(String cityName) {
        return null;
    }

    @Override
    public List<City> findByStateName(String stateName) {
        return null;
    }

    @Override
    public List<City> findByStateId(int stateId) {

        if (stateId <= 0) {
            return null;
        }

        return restService.get(resource + "?stateId=" + stateId);
    }
}
