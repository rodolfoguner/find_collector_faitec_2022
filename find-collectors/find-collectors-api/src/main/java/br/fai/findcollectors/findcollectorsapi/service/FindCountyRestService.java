package br.fai.findcollectors.findcollectorsapi.service;

import br.fai.findcollectors.entities.County;

import java.util.List;

public interface FindCountyRestService {

    List<County> find();

    County findById();

    List<County> findByState();

}
