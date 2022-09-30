package br.fai.findcollectors.findcollectorsdatabase.connection;

import java.sql.*;

public class ConnectionFactory {

    private static final String DATABASE = "find-collectors";
    private static final String PORT = "3000";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:" + PORT + "/" + DATABASE + "";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        closeConnection(connection);
        closePreparedStatement(preparedStatement);
        closeResultSet(resultSet);
    }

    public static void close(Connection connection, PreparedStatement preparedStatement) {
        closeConnection(connection);
        closePreparedStatement(preparedStatement);
    }


    private static void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }

        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement == null) {
            return;
        }

        try {
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
