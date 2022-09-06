package br.fai.findcollectors.findcollectorsapi.service.impl;

import br.fai.findcollectors.entities.County;
import br.fai.findcollectors.findcollectorsapi.service.FindCountyRestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCountyRestServiceImpl implements FindCountyRestService {

    @Override
    public List<County> find() {
        return null;
    }

    @Override
    public County findById() {
        return null;
    }

    @Override
    public List<County> findByState() {
        return null;
    }
}
