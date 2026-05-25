package org.example.practica5.Router;

import javafx.scene.control.Alert;
import org.example.practica5.Model.Order;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Repository.OrderRepository;
import org.example.practica5.Service;

public class OrderRoutes {

    private Service service;
    private OrderRepository orderRepository;

    public OrderRoutes(OrderRepository orderRepository) {
        this.service = Service.getInstance();
        this.orderRepository = orderRepository;
    }

    public void deleteOrder(Order order) {
        if (service.hasAccess(RoleUser.ADMIN) ) {
            if (order != null) {
                orderRepository.deleteOrder(order.getOrderNumber());
                service.showAlert("Подтверждение", "Заказ удален", Alert.AlertType.INFORMATION);
            } else {
                service.showAlert("Ошибка", "Заказ не выбран", Alert.AlertType.ERROR);
            }
        } else {
            service.showAlert("Ошибка", "У вас нет прав на удаление заказа", Alert.AlertType.ERROR);
        }
    }

    public void addOrder() {
        if (service.hasAccess(RoleUser.ADMIN) ) {

            service.changeScene("/org/example/practica5/AddEditOrder.fxml", "Добавление заказа");
        }
    }

    public void editOrder(Order order) {
        if (service.hasAccess(RoleUser.ADMIN) ) {
            if (order != null) {
                service.changeScene("/org/example/practica5/AddEditOrder.fxml", "Редактирование заказа");
            } else {
                service.showAlert("Ошибка", "Выберите заказ для редактирования", Alert.AlertType.WARNING);
            }
        }
    }
}