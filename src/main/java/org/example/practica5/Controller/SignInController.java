package org.example.practica5.Controller;

import javafx.stage.Stage;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.User;
import org.example.practica5.Repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.practica5.Router.GoustRoute;
import org.example.practica5.Service;

import java.sql.SQLException;

public class SignInController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    private UserRepository userRepository;
    private Service service;
    private GoustRoute goustRoute;

    @FXML
    private void initialize() {
        DBHandler dbHandler = new DBHandler();
        userRepository = new UserRepository(dbHandler);
        goustRoute = new GoustRoute();
        service = Service.getInstance();
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty()) {
            service.showAlert("Ошибка", "Введите логин", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        if (password.isEmpty()) {
            service.showAlert("Ошибка", "Введите пароль", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        User user = null;
        try {
            user = userRepository.authenticate(login, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (user == null) {
            service.showAlert("Ошибка", "Неверный логин или пароль", javafx.scene.control.Alert.AlertType.ERROR);
            return;
        }

        service.setCurrentUser(user);

        service.changeScene("/org/example/practica5/ProductList.fxml", "Список товаров");
    }

    @FXML
    private void handleGuestLogin() {

        goustRoute.loginAsGuest();
    }
}