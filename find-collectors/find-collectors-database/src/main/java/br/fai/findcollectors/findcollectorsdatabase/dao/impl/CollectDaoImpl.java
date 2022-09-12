package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CollectDao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CollectDaoImpl implements CollectDao<Collect> {
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
                Collect collect = new Collect();

                collect.setId(resultSet.getInt("id"));
                collect.setDateAndTime(resultSet.getTimestamp("data_e_hora"));
                collect.setAccept(resultSet.getBoolean("aceito"));
                collect.setCollected(resultSet.getBoolean("coletado"));
                collect.setRecurrent(resultSet.getBoolean("recorrente"));
                collect.setCep(resultSet.getString("cep"));
                collect.setAddress(resultSet.getString("endereco"));
                collect.setDistrict(resultSet.getString("bairro"));
                collect.setNumber(resultSet.getString("numero"));
                collect.setCreatedAt(resultSet.getTimestamp("criado_em"));
                collect.setLastModified(resultSet.getTimestamp("alterado_em"));

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
        return null;
    }
}