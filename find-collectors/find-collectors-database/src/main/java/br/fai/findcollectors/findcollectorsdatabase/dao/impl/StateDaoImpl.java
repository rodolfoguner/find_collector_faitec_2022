package br.fai.findcollectors.findcollectorsdatabase.dao.impl;

import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsdatabase.connection.ConnectionFactory;
import br.fai.findcollectors.findcollectorsdatabase.dao.StateDao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StateDaoImpl implements StateDao {

    @Override
    public List<State> find() {

        List<State> states = new ArrayList<State>();

        final String sql = "SELECT * FROM estado e ORDER BY e.nome_estado";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                State state = loadValues(resultSet);

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
    public State findById(int id) {

        State state = null;

        final String sql = "SELECT * FROM estado e WHERE e.id = ?;";

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

            state = loadValues(resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(connection, preparedStatement, resultSet);
        }

        return state;
    }

    @Override
    public List<State> findStateByName(String stateName) {


        List<State> states = new ArrayList<>();

        final String sql = "SELECT * FROM estado e WHERE e.nome_estado ILIKE ?;";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, stateName + "%");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                State state = loadValues(resultSet);

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
    public State loadValues(ResultSet resultSet) throws SQLException {
        State state = new State();
        state.setId(resultSet.getInt("id"));
        state.setStateName(resultSet.getString("nome_estado"));
        state.setUf(resultSet.getString("uf"));
        state.setCreatedAt(resultSet.getTimestamp("criado_em"));
        state.setLastModified(resultSet.getTimestamp("alterado_em"));
        return state;
    }


}
