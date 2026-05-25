package org.example.practica5.Router;

import javafx.scene.Node;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Model.User;
import org.example.practica5.Service;

public class GoustRoute {

    private Service service;

    public GoustRoute() {
        this.service = Service.getInstance();
    }


    public void loginAsGuest() {
        User guest = new User("Гость", "guest", RoleUser.GUEST);
        service.setCurrentUser(guest);
        service.changeScene("/org/example/practica5/ProductList.fxml", "Список товаров");
    }
}