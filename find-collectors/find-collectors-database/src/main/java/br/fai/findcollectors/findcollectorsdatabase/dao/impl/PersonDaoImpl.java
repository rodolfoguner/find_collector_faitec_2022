package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonDaoImpl implements PersonDao<Person> {

    @Autowired
    CityDao cityDao;

    @Override
    public List<Person> find() {

        List<Person> personList = new ArrayList<>();

        final String sql = "SELECT * FROM pessoa p ORDER BY p.nome;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Person person = loadValues(resultSet);

                personList.add(person);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return personList;
    }

    @Override
    public Person findById(int id) {
        return null;
    }

    @Override
    public int create(Person entity) {
        return 0;
    }

    @Override
    public boolean update(Person entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public Person loadValues(ResultSet resultSet) throws SQLException {

        Person person = new Person();
        City city = cityDao.findById(resultSet.getInt("municipio_id"));

        person.setId(resultSet.getInt("id"));
        person.setEmail(resultSet.getString("email"));
        person.setName(resultSet.getString("nome"));
        person.setTelephone(resultSet.getString("telefone"));
        person.setCep(resultSet.getString("cep"));
        person.setAddress(resultSet.getString("endereco"));
        person.setDistrict(resultSet.getString("bairro"));
        person.setNumber(resultSet.getString("numero"));
        person.setCityId(city);
        person.setCreatedAt(resultSet.getTimestamp("criado_em"));
        person.setLastModified(resultSet.getTimestamp("alterado_em"));

        return person;
    }
}
