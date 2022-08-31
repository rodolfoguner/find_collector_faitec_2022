package br.fai.findcollectors.findcollectorsdatabase.connection;

import java.sql.*;

public class ConnectionFactory {

    private static final String DATABASE = System.getenv("POSTGRES_DB").isEmpty() ? System.getenv("POSTGRES_DB") : "find-collectors";
    private static final String PORT = System.getenv("DB_PORT").isEmpty() ? System.getenv("DB_PORT") : "5432";
    private static final String USERNAME = System.getenv("POSTGRES_USER").isEmpty() ? System.getenv("POSTGRES_USER") : "postgres";
    private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD").isEmpty() ? System.getenv("POSTGRES_PASSWORD") : "postgres";
    private static final String URL = "jdbc:postgresql://localhost:" + PORT + "/" + DATABASE + "";

    private static Connection connection = null;

    private ConnectionFactory() {
    }

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
