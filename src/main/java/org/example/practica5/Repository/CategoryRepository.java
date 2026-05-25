package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private DBHandler dbHandler;

    public CategoryRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM product_categories";

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("category")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}