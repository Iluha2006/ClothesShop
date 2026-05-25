package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.ProductName;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private static final String IMAGE_BASE_DIR = "C:/Users/user/Desktop/img/";
    private static final String DEFAULT_IMAGE_PATH = "C:/Users/user/Desktop/picture.png";
    private final DBHandler dbHandler;

    public ProductRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public String buildImageUrl(String image) {
        if (image == null || image.isEmpty()) {
            return new File(DEFAULT_IMAGE_PATH).toURI().toString();
        }
        File file = new File(IMAGE_BASE_DIR + new File(image).getName());
        if (file.exists()) {
            return file.toURI().toString();
        } else {
            return new File(DEFAULT_IMAGE_PATH).toURI().toString();
        }
    }


    private int getProviderId(String providerName) {
        String query = "SELECT id FROM providers WHERE provider_name = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, providerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int getManufacturerId(String manufacturerName) {
        String query = "SELECT id FROM manufacturers WHERE manufacturer = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, manufacturerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int getCategoryId(String categoryName) {
        String query = "SELECT id FROM product_categories WHERE category = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = """
                    SELECT 
                           p.article, p.quantity, p.unit, p.price,
                           p.image, p.product_description, p.current_discount,
                           pr.provider_name, 
                           m.manufacturer as manufacturer_name,
                           pc.category as category_name,
                           pn.name as product_name_text
                    FROM products p
                    LEFT JOIN providers pr ON p.provider = pr.id
                    LEFT JOIN manufacturers m ON p.manufacturer = m.id
                    LEFT JOIN product_categories pc ON p.product_category = pc.id
                    LEFT JOIN product_names pn ON p.product_name = pn.id
                    ORDER BY p.article
                """;

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("article"),
                        rs.getInt("quantity"),
                        rs.getString("unit"),
                        rs.getDouble("price"),
                        rs.getString("provider_name"),
                        rs.getString("manufacturer_name"),
                        rs.getString("category_name"),
                        rs.getString("product_name_text"),
                        rs.getInt("current_discount"),
                        rs.getString("product_description"),
                        rs.getString("image")
                );
                product.setImagePath(buildImageUrl(rs.getString("image")));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }



    private int getProductName(String productName) {

        String selectQuery = "SELECT id FROM product_names WHERE name = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(selectQuery)) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



    public void addProduct(Product product) {
        int providerId = getProviderId(product.getProviderName());
        int manufacturerId = getManufacturerId(product.getManufacturerName());
        int categoryId = getCategoryId(product.getCategoryName());

        int name = getProductName(product.getProductName());

        String query = """
        INSERT INTO products (article, quantity, unit, price, provider, 
        manufacturer, product_category, product_name, current_discount, 
        product_description, image)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, product.getArticle());
            stmt.setInt(2, product.getQuantity());
            stmt.setString(3, product.getUnit());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, providerId);
            stmt.setInt(6, manufacturerId);
            stmt.setInt(7, categoryId);
            stmt.setInt(8, name );
            stmt.setInt(9, product.getCurrentDiscount());
            stmt.setString(10, product.getProductDescription());
            stmt.setString(11, product.getImagePath());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Добавлено строк: " + rowsAffected);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    public void updateProduct(Product product) {
        int providerId = getProviderId(product.getProviderName());
        int manufacturerId = getManufacturerId(product.getManufacturerName());
        int categoryId = getCategoryId(product.getCategoryName());

        int productNameId = getProductName(product.getProductName());


        updateProductName(product.getProductName(), productNameId);

        String query = """
        UPDATE products SET quantity=?, unit=?, price=?, provider=?, 
        manufacturer=?, product_category=?, product_name=?, 
        current_discount=?, product_description=?, image=?
        WHERE article=?
    """;

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setInt(1, product.getQuantity());
            stmt.setString(2, product.getUnit());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, providerId);
            stmt.setInt(5, manufacturerId);
            stmt.setInt(6, categoryId);
            stmt.setInt(7, productNameId);
            stmt.setInt(8, product.getCurrentDiscount());
            stmt.setString(9, product.getProductDescription());
            stmt.setString(10, product.getImagePath());
            stmt.setString(11, product.getArticle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateProductName(String productName, int productNameId) {
        String query = "UPDATE product_names SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, productName);
            stmt.setInt(2, productNameId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteProduct(String article) {
        String query = "DELETE FROM products WHERE article = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, article);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}