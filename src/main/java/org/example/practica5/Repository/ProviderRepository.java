package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Provider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProviderRepository {
    private DBHandler dbHandler;

    public ProviderRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public List<Provider> getAllProviders() {
        List<Provider> providers = new ArrayList<>();
        String query = "SELECT * FROM providers";

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                providers.add(new Provider(rs.getInt("id"), rs.getString("provider_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return providers;
    }
}