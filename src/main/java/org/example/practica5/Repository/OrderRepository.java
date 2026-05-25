package org.example.practica5.Repository;

import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Order;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.User;
import org.example.practica5.Model.PickUpPoint;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final DBHandler dbHandler;

    public OrderRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = """
            SELECT
                o.order_number,
                o.order_article,
                o.order_status,
                o.order_date,
                o.delivery_date,
                o.code_to_receive,
                u.full_name,
                CONCAT(pp."index", ', ', pp.city, ', ', pp.street, ', ',
                       COALESCE(pp.building_number::text, '')) AS full_address
            FROM orders AS o
            LEFT JOIN pick_up_points AS pp ON o.pick_up_address = pp."index"
            LEFT JOIN users AS u ON o.client = u.id
            ORDER BY o.order_number
        """;

        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_number"),
                        rs.getString("order_article"),
                        rs.getString("order_status"),
                        rs.getString("order_date"),
                        rs.getString("delivery_date"),
                        rs.getString("full_address"),
                        rs.getString("full_name"),
                        rs.getInt("code_to_receive")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<User> getAllClients() {
        List<User> clients = new ArrayList<>();
        String query = "SELECT id, full_name FROM users ORDER BY full_name";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clients.add(new User(rs.getInt("id"), rs.getString("full_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public List<PickUpPoint> getAllPickUpPoints() {
        List<PickUpPoint> points = new ArrayList<>();
        String query = "SELECT \"index\", city, street, building_number FROM pick_up_points ORDER BY city";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                points.add(new PickUpPoint(
                        rs.getInt("index"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getInt("building_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }

    public List<Product> getAllProductsForOrder() {
        List<Product> products = new ArrayList<>();
        String query = """
            SELECT DISTINCT p.article, pn.name
            FROM products p
            INNER JOIN product_names pn ON p.product_name = pn.id
            ORDER BY pn.name
        """;
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(rs.getString("article"), rs.getString("name"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return Date.valueOf(LocalDate.now());
        }
        try {
            return Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                return Date.valueOf(localDate);
            } catch (Exception ex) {
                return Date.valueOf(LocalDate.now());
            }
        }
    }


    private int getPickUpPointIndex(String address) {
        if (address == null || address.isEmpty()) return 1;
        String query = "SELECT \"index\" FROM pick_up_points WHERE CONCAT(\"index\", ', ', city, ', ', street, ', ', building_number) = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("index");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }


    private int getClientId(String clientName) {
        if (clientName == null || clientName.isEmpty()) return 1;
        String query = "SELECT id FROM users WHERE full_name = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setString(1, clientName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int addOrder(Order order, String productArticle, int quantity) {
        try {
            String insertOrder = """
            INSERT INTO orders (order_number, order_article, order_date, delivery_date,
            pick_up_address, client, code_to_receive, order_status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
            try (PreparedStatement stmt = dbHandler.getPreparedStatement(insertOrder)) {
                stmt.setInt(1, order.getOrderNumber());
                stmt.setString(2, order.getOrderArticle());
                stmt.setDate(3, parseDate(order.getOrderDate()));
                stmt.setDate(4, parseDate(order.getDeliveryDate()));
                stmt.setInt(5, getPickUpPointIndex(order.getPickUpAddress()));
                stmt.setInt(6, getClientId(order.getClientName()));
                stmt.setInt(7, order.getCodeToReceive());
                stmt.setString(8, order.getOrderStatus());
                stmt.executeUpdate();
            }

            addProductToOrder(order.getOrderNumber(), productArticle, quantity);
            return 1;
        } catch (SQLException e) {
            System.err.println("SQL Error при добавлении заказа: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public void addProductToOrder(int orderNumber, String productArticle, int quantity) {
        int nextId = getNextProductOrderId();
        String query = "INSERT INTO products_orders (id, product, product_quantity, \"order\") VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setInt(1, nextId);
            stmt.setString(2, productArticle);
            stmt.setInt(3, quantity);
            stmt.setInt(4, orderNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getNextProductOrderId() {
        String query = "SELECT COALESCE(MAX(id), 0) + 1 FROM products_orders";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void updateOrder(Order order, String productArticle, int quantity) {
        String updateOrder = """
            UPDATE orders SET order_article=?, order_date=?, delivery_date=?,
            pick_up_address=?, client=?, code_to_receive=?, order_status=?
            WHERE order_number=?
        """;
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(updateOrder)) {
            stmt.setString(1, order.getOrderArticle());
            stmt.setDate(2, parseDate(order.getOrderDate()));
            stmt.setDate(3, parseDate(order.getDeliveryDate()));
            stmt.setInt(4, getPickUpPointIndex(order.getPickUpAddress()));
            stmt.setInt(5, getClientId(order.getClientName()));
            stmt.setInt(6, order.getCodeToReceive());
            stmt.setString(7, order.getOrderStatus());
            stmt.setInt(8, order.getOrderNumber());
            stmt.executeUpdate();

            deleteProductsFromOrder(order.getOrderNumber());
            addProductToOrder(order.getOrderNumber(), productArticle, quantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int orderNumber) {
        deleteProductsFromOrder(orderNumber);
        String query = "DELETE FROM orders WHERE order_number = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setInt(1, orderNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProductsFromOrder(int orderNumber) {
        String query = "DELETE FROM products_orders WHERE \"order\" = ?";
        try (PreparedStatement stmt = dbHandler.getPreparedStatement(query)) {
            stmt.setInt(1, orderNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}