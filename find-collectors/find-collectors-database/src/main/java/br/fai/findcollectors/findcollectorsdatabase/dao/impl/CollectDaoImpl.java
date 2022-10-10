package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.Collect;
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
    PersonDao personDao;

    @Override
    public List<Collect> find() {
        List<Collect> collects = new ArrayList<Collect>();

        final String sql = "SELECT * FROM coleta";

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
        return null;
    }

    @Override
    public int create(Collect entity) {
        return 0;
    }

    @Override
    public boolean update(Collect entity) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public Collect loadValues(ResultSet resultSet) throws SQLException {

        Collect collect = new Collect();
        City city = cityDao.findById(resultSet.getInt("municipio_id"));
        Array garbageTypes = resultSet.getArray("tipo_lixo");
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
        collect.setCollector();
        collect.setCreatedAt(resultSet.getTimestamp("criado_em"));
        collect.setLastModified(resultSet.getTimestamp("alterado_em"));

        return collect;
    }
}