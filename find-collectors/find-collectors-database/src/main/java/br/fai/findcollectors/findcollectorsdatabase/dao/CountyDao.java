package br.fai.findcollectors.findcollectorsdatabase.dao;

import br.fai.findcollectors.entities.County;

import java.util.List;

public interface CountyDao {

    List<County> find();

    County findById();

    List<County> findByState();

}
