package org.example.practica5.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.practica5.Model.Order;
import org.example.practica5.Service;

public class OrderCardController {

    @FXML private AnchorPane rootPane;
    @FXML private Label lblOrderNumber;
    @FXML private Label lblOrderArticle;
    @FXML private Label lblStatus;
    @FXML private Label lblOrderDate;
    @FXML private Label lblDeliveryDate;
    @FXML private Label lblPickUpAddress;
    @FXML private Label lblClientName;
    @FXML private Label lblCodeToReceive;

    private Order currentOrder;
    private Service service;

    @FXML
    private void initialize() {
        service = Service.getInstance();

        rootPane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editOrder();
            }
        });
    }

    public void setOrderCardData(Order order) {
        this.currentOrder = order;

        if (order == null) return;

        lblOrderNumber.setText("Заказ №" + order.getOrderNumber());

        String status = order.getOrderStatus();
        lblStatus.setText(status);
        if ("Новый".equals(status)) {
            lblStatus.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        } else if ("Завершен".equals(status)) {
            lblStatus.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        } else {
            lblStatus.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 3 10 3 10;");
        }

        lblOrderArticle.setText(order.getOrderArticle());
        lblClientName.setText(order.getClientName());
        lblOrderDate.setText(order.getOrderDate());

        String deliveryDate = order.getDeliveryDate();
        lblDeliveryDate.setText(deliveryDate != null ? deliveryDate : "не указана");
        lblPickUpAddress.setText(order.getPickUpAddress());

        int code = order.getCodeToReceive();
        lblCodeToReceive.setText(code > 0 ? String.valueOf(code) : "не указан");

    }

    private void editOrder() {
        if (currentOrder != null) {
            service.setOrderToEdit(currentOrder);
            service.changeScene("/org/example/practica5/AddEditOrder.fxml", "Редактирование заказа");
        }
    }

}