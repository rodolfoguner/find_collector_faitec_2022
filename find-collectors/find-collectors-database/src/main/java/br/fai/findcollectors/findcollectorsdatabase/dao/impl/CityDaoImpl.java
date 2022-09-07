package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CityDaoImpl implements CityDao {
    @Override
    public List<City> find() {

        List<City> cities = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                City city = new City();
                State state = new State();

                city.setId(resultSet.getInt("id"));
                city.setCity(resultSet.getString("cidade"));
                city.setCreatedAt(resultSet.getTimestamp("criado_em"));
                city.setLastModified(resultSet.getTimestamp("alterado_em"));

                state.setId(resultSet.getInt("estado_id"));
                state.setUf(resultSet.getString("uf"));
                state.setStateName(resultSet.getString("nome_estado"));
                state.setCreatedAt(resultSet.getTimestamp("criado_em"));
                state.setLastModified(resultSet.getTimestamp("alterado_em"));

                city.setStateId(state);

                cities.add(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return cities;
    }

    @Override
    public City findById(int id) {
        return null;
    }

    @Override
    public List<City> findByState(String stateName) {
        return null;
    }

    @Override
    public List<City> findCityByName(String cityName) {
        return null;
    }

}
