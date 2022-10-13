package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CityDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.CollectDao;
import br.fai.findcollectors.findcollectorsdatabase.dao.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CollectDaoImpl implements CollectDao<Collect> {

    @Autowired
    CityDao cityDao;

    @Autowired
    PersonDao<Person> personDao;

    @Override
    public List<Collect> find() {
        List<Collect> collects = new ArrayList<>();

        final String sql = "SELECT * FROM coleta ORDER BY id DESC;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Collect collect = loadValues(resultSet);

                collects.add(collect);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }
        return collects;
    }

    @Override
    public Collect findById(int id) {

        Collect collect = null;

        final String sql = "SELECT * FROM coleta WHERE id = ?";

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

            collect = loadValues(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return collect;
    }

    @Override
    public int create(Collect entity) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int id = -1;

        try {

            final String sql = "INSERT INTO coleta " +
                    "(id," +
                    "data_e_hora, " +
                    "tipo_de_lixo, " +
                    "recorrente, " +
                    "cep, " +
                    "endereco, " +
                    "bairro, " +
                    "numero, " +
                    "municipio_id, " +
                    "reciclador_id, " +
                    "criado_em) " +
                    "VALUES " +
                    "(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW());";

            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            Array garbageType = connection.createArrayOf("tipo_lixo", entity.getGarbageType().toArray());

            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, entity.getDateAndTime());
            preparedStatement.setArray(2, garbageType);
            preparedStatement.setBoolean(3, entity.isRecurrent());
            preparedStatement.setString(4, entity.getCep());
            preparedStatement.setString(5, entity.getAddress());
            preparedStatement.setString(6, entity.getDistrict());
            preparedStatement.setString(7, entity.getNumber());
            preparedStatement.setInt(8, entity.getCityId());
            preparedStatement.setInt(9, entity.getRecyclerId());


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
    public boolean update(Collect entity) {

        final String sql = "UPDATE coleta SET " +
                "data_e_hora = ?," +
                "tipo_de_lixo = ?,   " +
                "recorrente = ?, " +
                "cep = ?, " +
                "endereco = ?, " +
                "bairro = ?," +
                "numero = ?," +
                "municipio_id = ?," +
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
            preparedStatement.setTimestamp(1, entity.getDateAndTime());
            preparedStatement.setArray(2, garbageType);
            preparedStatement.setBoolean(3, entity.isRecurrent());
            preparedStatement.setString(4, entity.getCep());
            preparedStatement.setString(5, entity.getAddress());
            preparedStatement.setString(6, entity.getDistrict());
            preparedStatement.setString(7, entity.getNumber());
            preparedStatement.setInt(8, entity.getCityId());
            preparedStatement.setInt(9, entity.getId());
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

        final String sql = "DELETE FROM coleta c WHERE c.id = ?;";

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
    public Collect loadValues(ResultSet resultSet) throws SQLException {

        Collect collect = new Collect();
        City city = cityDao.findById(resultSet.getInt("municipio_id"));
        Person collector = personDao.findById(resultSet.getInt("catador_id"));
        Person recycler = personDao.findById(resultSet.getInt("reciclador_id"));
        Array garbageTypes = resultSet.getArray("tipo_de_lixo");
        List<GarbageType> garbageTypeList = new ArrayList<>();

        if (garbageTypes != null) {

            String garbageTypesArray[] = (String[]) garbageTypes.getArray();

            for (String garbageType : garbageTypesArray) {

                garbageTypeList.add(Enum.valueOf(GarbageType.class, garbageType));
            }
        }

        collect.setId(resultSet.getInt("id"));
        collect.setDateAndTime(resultSet.getTimestamp("data_e_hora"));
        collect.setGarbageType(garbageTypeList);
        collect.setAccept(resultSet.getBoolean("aceito"));
        collect.setCollected(resultSet.getBoolean("coletado"));
        collect.setRecurrent(resultSet.getBoolean("recorrente"));
        collect.setCep(resultSet.getString("cep"));
        collect.setAddress(resultSet.getString("endereco"));
        collect.setDistrict(resultSet.getString("bairro"));
        collect.setNumber(resultSet.getString("numero"));
        collect.setCity(city);
        collect.setCityId(resultSet.getInt("municipio_id"));
        collect.setCollector(collector);
        collect.setCollectorId(resultSet.getInt("catador_id"));
        collect.setRecycler(recycler);
        collect.setRecyclerId(resultSet.getInt("reciclador_id"));
        collect.setCreatedAt(resultSet.getTimestamp("criado_em"));
        collect.setLastModified(resultSet.getTimestamp("alterado_em"));

        return collect;
    }

    @Override
    public boolean acceptCollect(Collect entity) {

        final String sql = "UPDATE coleta SET " +
                "aceito = ?," +
                "catador_id = ?, " +
                "alterado_em = NOW() " +
                "WHERE " +
                "id = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;


        try {

            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBoolean(1, entity.isAccept());
            preparedStatement.setInt(2, entity.getCollectorId());
            preparedStatement.setInt(3, entity.getId());
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
    public boolean closeCollect(Collect entity) {

        final String sql = "UPDATE coleta SET " +
                "coletado = ?," +
                "alterado_em = NOW() " +
                "WHERE " +
                "id = ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;


        try {

            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBoolean(1, entity.isCollected());
            preparedStatement.setInt(2, entity.getId());
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
    public List<Collect> findFreeCollects() {
        List<Collect> collects = new ArrayList<Collect>();

        final String sql = "SELECT * FROM coleta C" +
                " WHERE C.aceito = FALSE" +
                " AND C.coletado= FALSE;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Collect collect = loadValues(resultSet);

                collects.add(collect);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }
        return collects;
    }
}