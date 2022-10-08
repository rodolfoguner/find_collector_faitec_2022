package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonDaoImpl implements PersonDao<Person> {

    @Autowired
    CityDao cityDao;

    @Override
    public List<Person> find() {

        List<Person> personList = new ArrayList<Person>();

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

        Person person = null;

        final String sql = "SELECT * FROM pessoa p WHERE p.id = ?";

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

            person = loadValues(resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return person;

    }

    @Override
    public int create(Person entity) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int id = -1;

        try {

            final String sql = "INSERT INTO pessoa (id, email, senha) VALUES (DEFAULT, ?, ?);";

            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setString(2, entity.getPassword());

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }

            connection.commit();

            return id;
        } catch (Exception e) {
            e.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }

            return id;
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public boolean update(Person entity) {

        final String sql = "UPDATE pessoa SET " +
                "nome = ?, " +
                "telefone = ?, " +
                "cep = ?, " +
                "endereco = ?, " +
                "bairro = ?, " +
                "numero = ?, " +
                "municipio_id = ?," +
                "descricao = ?, " +
                "tipo_pessoa = ?::\"tipo_pessoa\", " +
                "tipo_lixo = ?, " +
                "ponto_coleta = ?, " +
                "alterado_em = NOW() " +
                "WHERE " +
                "id = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;


        try {

            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            Array garbageType = connection.createArrayOf("tipo_lixo", entity.getGarbageType().toArray());

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getTelephone());
            preparedStatement.setString(3, entity.getCep());
            preparedStatement.setString(4, entity.getAddress());
            preparedStatement.setString(5, entity.getDistrict());
            preparedStatement.setString(6, entity.getNumber());
            preparedStatement.setInt(7, entity.getCityId());
            preparedStatement.setString(8, entity.getDescription());
            preparedStatement.setString(9, entity.getPersonType().toString());
            preparedStatement.setArray(10, garbageType);
            preparedStatement.setBoolean(11, entity.isCollectPoint());
            preparedStatement.setInt(12, entity.getId());
            preparedStatement.execute();

            connection.commit();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.commit();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            return false;

        } finally {
            ConnectionFactory.close(connection, preparedStatement);
        }
    }

    @Override
    public boolean deleteById(int id) {

        boolean result = false;

        final String sql = "DELETE FROM pessoa p WHERE p.id = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            connection.commit();

            result = true;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                connection.rollback();

            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        } finally {

            ConnectionFactory.close(connection, preparedStatement);

        }

        return result;
    }

    @Override
    public Person findPersonByEmail(String email) {

        Person person = null;


        final String sql = "SELECT * FROM pessoa p WHERE p.email = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            person = loadValues(resultSet);
            person.setPassword(resultSet.getString("senha"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return person;
    }

    @Override
    public Person loadValues(ResultSet resultSet) throws SQLException {

        Person person = new Person();
        Person godfather = this.findById(resultSet.getInt("padrinho_id"));
        City city = cityDao.findById(resultSet.getInt("municipio_id"));
        String personType = resultSet.getString("tipo_pessoa");
        Array garbageTypes = resultSet.getArray("tipo_lixo");
        List<GarbageType> garbageTypeList = new ArrayList<>();

        if (garbageTypes != null) {

            String garbageTypesArray[] = (String[]) garbageTypes.getArray();

            for (String garbageType : garbageTypesArray) {

                garbageTypeList.add(Enum.valueOf(GarbageType.class, garbageType));
            }
        }

        person.setId(resultSet.getInt("id"));
        person.setEmail(resultSet.getString("email"));
        person.setName(resultSet.getString("nome"));
        person.setTelephone(resultSet.getString("telefone"));
        person.setCep(resultSet.getString("cep"));
        person.setAddress(resultSet.getString("endereco"));
        person.setDistrict(resultSet.getString("bairro"));
        person.setNumber(resultSet.getString("numero"));
        person.setGodfather(godfather);
        person.setGodfatherId(resultSet.getInt("padrinho_id"));
        person.setPersonType(Enum.valueOf(PersonType.class, personType));
        person.setGarbageType(garbageTypeList);
        person.setCity(city);
        person.setCityId(resultSet.getInt("municipio_id"));
        person.setCollectPoint(resultSet.getBoolean("ponto_coleta"));
        person.setCreatedAt(resultSet.getTimestamp("criado_em"));
        person.setLastModified(resultSet.getTimestamp("alterado_em"));

        return person;
    }
}