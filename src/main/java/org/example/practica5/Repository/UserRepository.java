package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private DBHandler dbHandler;

    public UserRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    public User authenticate(String login, String password) throws SQLException {
        String query = """
            SELECT u.id, u.full_name, u.login, u.password, 
                   ur.role as role_name
            FROM users u
            LEFT JOIN user_roles ur ON u.worker_role = ur.id
            WHERE u.login = ? AND u.password = ?
        """;


        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {

            stmt.setString(1, login);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String roleName = rs.getString("role_name");

                return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("login"),
                        rs.getString("password"),
                        RoleUser.fromString(roleName)
                );
            }
        }

        return null;
    }


}