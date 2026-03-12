package br.fai.findcollectors.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {

    List<T> find();
    Optional<T> findById(Long id);
    T create(T entity);
    T update(T entity);
    void deleteById(Long id);
}
