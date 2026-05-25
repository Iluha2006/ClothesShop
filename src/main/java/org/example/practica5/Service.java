package org.example.practica5;

import org.example.practica5.Model.Order;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Model.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Service {
    private static User currentUser;
    private static Service instance;
    private Product productToEdit;
    private Stage primaryStage;


    private static Order orderToEdit;

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
    public void setOrderToEdit(Order order) {
        this.orderToEdit = order;
    }

    public Order getOrderToEdit() {
        return orderToEdit;
    }
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Product getProductToEdit() {
        return productToEdit;
    }

    public void setProductToEdit(Product product) {
        this.productToEdit = product;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasAccess(RoleUser requiredRole) {
        if (currentUser == null) return false;

        switch (requiredRole) {
            case ADMIN:
                return currentUser.getRole() == RoleUser.ADMIN;
            case MANAGER:
                return currentUser.getRole() == RoleUser.MANAGER;
            case CLIENT:
                return currentUser.getRole() == RoleUser.CLIENT;
            case GUEST:
                return true;
            default:
                return false;
        }
    }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void changeScene(String fxmlPath, String title) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML файл не найден: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root));
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу: " + fxmlPath, Alert.AlertType.ERROR);
        }
    }


}