package org.example.practica5.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.practica5.Model.Order;

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
    public void setOrderCardData(Order order) {
        lblOrderNumber.setText("Заказ №" + order.getOrderNumber());
        lblStatus.setText("Статус заказа "+ order.getOrderStatus());
        lblOrderArticle.setText(order.getOrderArticle());
        lblClientName.setText(order.getClientName());
        lblOrderDate.setText(order.getOrderDate());
        lblDeliveryDate.setText(order.getDeliveryDate());
        lblPickUpAddress.setText(order.getPickUpAddress());
        lblCodeToReceive.setText(String.valueOf(order.getCodeToReceive()));

    }
}