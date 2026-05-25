package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.ProductName;

import java.util.ArrayList;
import java.util.List;

public class ProductNameRepostory {

    private DBHandler dbHandler;

    public ProductNameRepostory(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public List<ProductName> loadProductNames() {
        List<ProductName> productNames = new ArrayList<>();
        String query = "SELECT id, name FROM product_names ORDER BY name";
        try {

            var rs = dbHandler.executeQuery(query);
            while (rs.next()) {

                ProductName productName = new ProductName(rs.getInt("id"), rs.getString("name"));
                productNames.add(productName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return productNames;
    }
}
