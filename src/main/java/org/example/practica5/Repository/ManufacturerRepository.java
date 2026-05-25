package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Manufacturer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerRepository {
    private final DBHandler dbHandler;

    public ManufacturerRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> manufacturers = new ArrayList<>();
        String query = "SELECT id, manufacturer FROM manufacturers ORDER BY manufacturer";

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                manufacturers.add(new Manufacturer(
                        rs.getInt("id"),
                        rs.getString("manufacturer")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manufacturers;
    }
}
