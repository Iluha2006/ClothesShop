package org.example.practica5.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Order;
import org.example.practica5.Model.PickUpPoint;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.User;
import org.example.practica5.Repository.OrderRepository;
import org.example.practica5.Service;

import java.time.LocalDate;
import java.util.List;

public class AddEditOrderController {

    @FXML private TextField orderNumberField;
    @FXML private TextField orderArticleField;
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
        statusComboBox.getItems().addAll("Новый", "Завершен");
    }

    private void checkEditMode() {
        currentOrder = service.getOrderToEdit();
        if (currentOrder != null) {
            isEditMode = true;
            btnSaveOrder.setText("Обновить");
            loadOrderData(currentOrder);

            orderNumberField.setDisable(true);
        } else {
            isEditMode = false;
            btnSaveOrder.setText("Сохранить");
            orderNumberField.setDisable(false);
            orderNumberField.setText("");
            orderArticleField.setText("");
            quantityField.setText("1");
            codeField.setText("");
            orderDatePicker.setValue(LocalDate.now());
            deliveryDatePicker.setValue(null);
        }
    }

    private void loadOrderData(Order order) {
        currentOrder = order;

        orderNumberField.setText(String.valueOf(order.getOrderNumber()));
        orderArticleField.setText(order.getOrderArticle());
        clientComboBox.setValue(order.getClientName());
        pickUpPointComboBox.setValue(order.getPickUpAddress());
        orderDatePicker.setValue(parseDate(order.getOrderDate()));
        deliveryDatePicker.setValue(parseDate(order.getDeliveryDate()));

        int codeToReceive = order.getCodeToReceive();
        codeField.setText(String.valueOf(codeToReceive));

        statusComboBox.setValue(order.getOrderStatus());


        String article = order.getOrderArticle();
        if (article != null && !article.isEmpty()) {
            for (String item : productComboBox.getItems()) {
                if (item.startsWith(article)) {
                    productComboBox.setValue(item);
                    break;
                }
            }
        }


        int quantity = orderRepository.getProductQuantity(order.getOrderNumber(), order.getOrderArticle());
        quantityField.setText(quantity > 0 ? String.valueOf(quantity) : "1");
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
            int orderNumber = Integer.parseInt(orderNumberField.getText());
            String orderArticle = orderArticleField.getText().trim();
            String productData = productComboBox.getValue();
            String productArticle = productData != null ? productData.split(" - ")[0] : "";
            int quantity = Integer.parseInt(quantityField.getText());
            String pickUpAddress = pickUpPointComboBox.getValue();
            String clientName = clientComboBox.getValue();
            int code = Integer.parseInt(codeField.getText());
            String status = statusComboBox.getValue();
            String orderDate = orderDatePicker.getValue().toString();
            String deliveryDate = deliveryDatePicker.getValue() != null ? deliveryDatePicker.getValue().toString() : null;

            Order order = new Order(
                    orderNumber,
                    orderArticle,
                    status,
                    orderDate,
                    deliveryDate,
                    pickUpAddress,
                    clientName,
                    code
            );

            if (isEditMode) {
                String oldProductArticle = currentOrder != null ? currentOrder.getOrderArticle() : productArticle;
                orderRepository.updateOrder(order, productArticle, quantity, oldProductArticle);
                service.showAlert("Успех", "Заказ успешно обновлен!", Alert.AlertType.INFORMATION);
            } else {
                orderRepository.addOrder(order, productArticle, quantity);
                service.showAlert("Успех", "Заказ успешно добавлен!", Alert.AlertType.INFORMATION);
            }

            service.setOrderToEdit(null);
            backToOrders();
        } catch (NumberFormatException e) {
            service.showAlert("Ошибка", "Проверьте правильность ввода: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (orderArticleField.getText().trim().isEmpty()) {
            service.showAlert("Ошибка", "Введите артикул заказа", Alert.AlertType.WARNING);
            return false;
        }
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
        if (quantityField.getText().isEmpty() || quantityField.getText().equals("0")) {
            service.showAlert("Ошибка", "Введите количество (больше 0)", Alert.AlertType.WARNING);
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