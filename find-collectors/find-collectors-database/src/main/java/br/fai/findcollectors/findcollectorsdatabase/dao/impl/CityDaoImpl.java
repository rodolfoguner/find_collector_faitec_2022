package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CityDaoImpl implements CityDao {
    @Override
    public List<City> find() {

        List<City> cities = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id ORDER BY m.cidade;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                City city = loadValues(resultSet);

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

        City city = null;

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id WHERE m.id = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            city = loadValues(resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return city;

    }

    @Override
    public List<City> findCityByName(String cityName) {


        List<City> cities = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id " +
                "WHERE m.cidade ILIKE ? " +
                "ORDER BY m.cidade;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cityName + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                City city = loadValues(resultSet);

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
    public List<City> findCitiesByStateName(String stateName) {

        List<City> cities = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id " +
                "WHERE e.nome_estado ILIKE ? " +
                "ORDER BY m.cidade;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, stateName + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                City city = loadValues(resultSet);

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
    public List<City> findByStateId(int id) {


        List<City> cities = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id " +
                "WHERE e.id = ? " +
                "ORDER BY m.cidade;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                City city = loadValues(resultSet);

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
    public List<City> findByStateIdAndCityName(int stateId, String cityName) {


        List<City> cities = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id " +
                "WHERE e.id = ? " +
                "AND m.cidade ILIKE ? " +
                "ORDER BY m.cidade;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, stateId);
            preparedStatement.setString(2, cityName + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                City city = loadValues(resultSet);

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
    public City loadValues(ResultSet resultSet) throws SQLException {
        City city = new City();
        State state = new State();

        city.setId(resultSet.getInt("id"));
        city.setCity(resultSet.getString("cidade"));
        city.setCreatedAt(resultSet.getTimestamp("criado_em"));
        city.setLastModified(resultSet.getTimestamp("alterado_em"));

        state.setId(resultSet.getInt("estado_id"));
        state.setUf(resultSet.getString("uf"));
        state.setStateName(resultSet.getString("nome_estado"));
        state.setCreatedAt(resultSet.getTimestamp(9));
        state.setLastModified(resultSet.getTimestamp(10));

        city.setStateId(state);
        return city;
    }

}
