package br.fai.findcollectors.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BaseRepository<T> {

    List<T> find();

    T findById(int id);

    int create(T entity);

    boolean update(T entity);

    boolean deleteById(int id);

    T loadValues(ResultSet resultSet) throws SQLException;
}
