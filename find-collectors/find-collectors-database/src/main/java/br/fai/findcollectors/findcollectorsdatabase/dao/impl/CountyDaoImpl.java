package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.County;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.CountyDao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CountyDaoImpl implements CountyDao {
    @Override
    public List<County> find() {

        List<County> counties = new ArrayList<>();

        final String sql = "SELECT * FROM municipio m INNER JOIN estado e ON e.id = m.estado_id;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                County county = new County();
                State state = new State();

                county.setId(resultSet.getInt("id"));
                county.setCity(resultSet.getString("cidade"));
                county.setCreatedAt(resultSet.getTimestamp("criado_em"));
                county.setLastModified(resultSet.getTimestamp("alterado_em"));

                state.setId(resultSet.getInt("estado_id"));
                state.setUf(resultSet.getString("uf"));
                state.setStateName(resultSet.getString("nome_estado"));
                state.setCreatedAt(resultSet.getTimestamp("criado_em"));
                state.setLastModified(resultSet.getTimestamp("alterado_em"));

                county.setStateId(state);

                counties.add(county);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return counties;
    }

    @Override
    public County findById() {
        return null;
    }

    @Override
    public List<County> findByState() {
        return null;
    }
}
