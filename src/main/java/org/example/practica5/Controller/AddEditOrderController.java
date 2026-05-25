package org.example.practica5.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Order;
import org.example.practica5.Model.PickUpPoint;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.User;
import org.example.practica5.Repository.OrderRepository;
import org.example.practica5.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddEditOrderController {


    @FXML private TextField orderNumberField;
    @FXML private ComboBox<String> clientComboBox;
    @FXML private ComboBox<String> pickUpPointComboBox;
    @FXML private ComboBox<String> productComboBox;
    @FXML private TextField quantityField;
    @FXML private DatePicker orderDatePicker;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextField codeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button btnSaveOrder;
    @FXML private Button btnBackOrders;
    @FXML private Button btnEditOrder;
    @FXML private Button btnDeleteOrder;
    private Service service;
    private OrderRepository orderRepository;
    private boolean isEditMode = false;
    private Order currentOrder;

    @FXML
    private void initialize() {
        service = Service.getInstance();
        DBHandler dbHandler = new DBHandler();
        orderRepository = new OrderRepository(dbHandler);


        loadComboBoxes();
        setupStatusComboBox();

        checkEditMode();

        btnSaveOrder.setOnAction(e -> saveOrder());
        btnBackOrders.setOnAction(e -> backToOrders());
    }


    private void loadComboBoxes() {

        List<User> clients = orderRepository.getAllClients();
        clientComboBox.getItems().clear();
        for (User client : clients) {
            clientComboBox.getItems().add(client.getFullName());
        }


        List<PickUpPoint> points = orderRepository.getAllPickUpPoints();
        pickUpPointComboBox.getItems().clear();
        for (PickUpPoint point : points) {
            String fullAddress = point.getIndex() + ", " + point.getCity() + ", " + point.getStreet() + ", " + point.getBuildingNumber();
            pickUpPointComboBox.getItems().add(fullAddress);
        }


        List<Product> products = orderRepository.getAllProductsForOrder();
        productComboBox.getItems().clear();
        for (Product product : products) {
            productComboBox.getItems().add(product.getArticle() + " - " + product.getProductName());
        }
    }

    private void setupStatusComboBox() {

        statusComboBox.getItems().addAll(
                "Новый",
                "Завершен"
        );
    }

    private void checkEditMode() {
        Order orderToEdit = service.getOrderToEdit();
        if (orderToEdit != null) {
            isEditMode = true;
            btnSaveOrder.setText("Обновить");
            loadOrderData(orderToEdit);
           
            orderNumberField.setDisable(true);

        } else {
            isEditMode = false;
            btnSaveOrder.setText("Сохранить");

            orderNumberField.setDisable(false);
            orderNumberField.setStyle("");
            orderNumberField.setText("");
            orderDatePicker.setValue(LocalDate.now());
        }
    }

    private void loadOrderData(Order order) {
        orderNumberField.setText(String.valueOf(order.getOrderNumber()));

        clientComboBox.setValue(order.getClientName());
        pickUpPointComboBox.setValue(order.getPickUpAddress());
        orderDatePicker.setValue(parseDate(order.getOrderDate()));
        deliveryDatePicker.setValue(parseDate(order.getDeliveryDate()));
        codeField.setText(String.valueOf(order.getCodeToReceive()));
        statusComboBox.setValue(order.getOrderStatus());
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) return LocalDate.now();
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
    private void saveOrder() {
        if (!validateFields()) return;

        try {
            System.out.println("=== СОХРАНЕНИЕ ЗАКАЗА ===");

            int  orderNumber = Integer.parseInt(orderNumberField.getText());
            System.out.println("Номер заказа: " + orderNumber);

            String productData = productComboBox.getValue();
            String productArticle = productData != null ? productData.split(" - ")[0] : "";
            System.out.println("Артикул товара: " + productArticle);

            int quantity = Integer.parseInt(quantityField.getText());
            System.out.println("Количество: " + quantity);

            String pickUpAddress = pickUpPointComboBox.getValue();
            System.out.println("Адрес пункта выдачи: " + pickUpAddress);

            String clientName = clientComboBox.getValue();
            System.out.println("Клиент: " + clientName);

            int code = Integer.parseInt(codeField.getText());
            System.out.println("Код получения: " + code);

            String status = statusComboBox.getValue();
            System.out.println("Статус: " + status);

            String orderDate = orderDatePicker.getValue().toString();
            String deliveryDate = deliveryDatePicker.getValue() != null ? deliveryDatePicker.getValue().toString() : null;
            System.out.println("Дата заказа: " + orderDate);
            System.out.println("Дата доставки: " + deliveryDate);

            Order order = new Order(
                    orderNumber,
                    productArticle,
                    status,
                    orderDate,
                    deliveryDate,
                    pickUpAddress,
                    clientName,
                    code
            );

            if (isEditMode) {
                orderRepository.updateOrder(order, productArticle, quantity);
                service.showAlert("Успех", "Заказ успешно обновлен!", Alert.AlertType.INFORMATION);
            } else {
                orderRepository.addOrder(order, productArticle, quantity);
                service.showAlert("Успех", "Заказ успешно добавлен!", Alert.AlertType.INFORMATION);
            }

            service.setOrderToEdit(null);
            backToOrders();
        } catch (NumberFormatException e) {
            service.showAlert("Ошибка", "Проверьте правильность ввода количества и кода: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            service.showAlert("Ошибка", "Ошибка при сохранении: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    private boolean validateFields() {
        if (clientComboBox.getValue() == null) {
            service.showAlert("Ошибка", "Выберите клиента", Alert.AlertType.WARNING);
            return false;
        }
        if (pickUpPointComboBox.getValue() == null) {
            service.showAlert("Ошибка", "Выберите пункт выдачи", Alert.AlertType.WARNING);
            return false;
        }
        if (productComboBox.getValue() == null) {
            service.showAlert("Ошибка", "Выберите товар", Alert.AlertType.WARNING);
            return false;
        }
        if (quantityField.getText().isEmpty()) {
            service.showAlert("Ошибка", "Введите количество", Alert.AlertType.WARNING);
            return false;
        }
        if (codeField.getText().isEmpty()) {
            service.showAlert("Ошибка", "Введите код получения", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void backToOrders() {
        service.changeScene("/org/example/practica5/OrdersList.fxml", "Список заказов");
    }
}