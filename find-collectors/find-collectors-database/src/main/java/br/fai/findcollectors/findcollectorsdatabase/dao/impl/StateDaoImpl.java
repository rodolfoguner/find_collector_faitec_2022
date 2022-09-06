package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.StateDao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StateDaoImpl implements StateDao {

    @Override
    public List<State> find() {

        List<State> states = new ArrayList<State>();

        final String sql = "SELECT * FROM estado";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                State state = new State();

                state.setId(resultSet.getInt("id"));
                state.setStateName(resultSet.getString("nome_estado"));
                state.setUf(resultSet.getString("uf"));
                state.setCreatedAt(resultSet.getTimestamp("criado_em"));
                state.setLastModified(resultSet.getTimestamp("alterado_em"));

                states.add(state);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return states;
    }

    @Override
    public State findById() {
        return null;
    }
}
