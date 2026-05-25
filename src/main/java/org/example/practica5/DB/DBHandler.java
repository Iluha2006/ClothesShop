package org.example.practica5.DB;

import java.sql.*;

public class DBHandler {

    private Connection connection;

    public void DBConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
           String url = "jdbc:postgresql://localhost:5432/Shop";
            String user = "postgres";
            String password = "23paper0843";
            connection = DriverManager.getConnection(url, user, password);

        }
         catch (SQLException e) {

            e.printStackTrace();
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(String query) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("No active database connection");
        }
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            return stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    public ResultSet executeQuery(String query) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("No active database connection");
        }
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            return stmt.executeQuery();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        if (connection == null || connection.isClosed()) {
            DBConnection();
        }
        return connection.prepareStatement(query);
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}