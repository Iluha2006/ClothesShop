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
        loadClients();
        loadPickUpPoints();
        loadProducts();
    }

    private void loadClients() {
        List<User> clients = orderRepository.getAllClients();
        clientComboBox.getItems().clear();
        clients.forEach(client -> clientComboBox.getItems().add(client.getFullName()));
    }

    private void loadPickUpPoints() {
        List<PickUpPoint> points = orderRepository.getAllPickUpPoints();
        pickUpPointComboBox.getItems().clear();
        points.forEach(point -> {
            String fullAddress = point.getIndex() + ", " + point.getCity() + ", " + point.getStreet() + ", " + point.getBuildingNumber();
            pickUpPointComboBox.getItems().add(fullAddress);
        });
    }

    private void loadProducts() {
        List<Product> products = orderRepository.getAllProductsForOrder();
        productComboBox.getItems().clear();
        products.forEach(product -> productComboBox.getItems().add(product.getArticle() + " - " + product.getProductName()));
    }

    private void setupStatusComboBox() {
        statusComboBox.getItems().addAll("Новый", "Завершен");
    }

    private void checkEditMode() {
        currentOrder = service.getOrderToEdit();
        if (currentOrder != null) {
            enableEditMode();
            loadOrderData(currentOrder);
        } else {
            enableAddMode();
        }
    }

    private void enableEditMode() {
        isEditMode = true;
        btnSaveOrder.setText("Обновить");
        orderNumberField.setDisable(true);
    }

    private void enableAddMode() {
        isEditMode = false;
        btnSaveOrder.setText("Сохранить");
        orderNumberField.setDisable(false);
        clearFields();
    }

    private void clearFields() {
        orderNumberField.setText("");
        orderArticleField.setText("");
        quantityField.setText("1");
        codeField.setText("");
        orderDatePicker.setValue(LocalDate.now());
        deliveryDatePicker.setValue(null);
        clientComboBox.setValue(null);
        pickUpPointComboBox.setValue(null);
        productComboBox.setValue(null);
        statusComboBox.setValue(null);
    }

    private void loadOrderData(Order order) {
        currentOrder = order;

        orderNumberField.setText(String.valueOf(order.getOrderNumber()));
        orderArticleField.setText(order.getOrderArticle());
        clientComboBox.setValue(order.getClientName());
        pickUpPointComboBox.setValue(order.getPickUpAddress());
        orderDatePicker.setValue(parseDate(order.getOrderDate()));
        deliveryDatePicker.setValue(parseDate(order.getDeliveryDate()));
        codeField.setText(String.valueOf(order.getCodeToReceive()));
        statusComboBox.setValue(order.getOrderStatus());

        selectProductInComboBox(order.getOrderArticle());
        loadProductQuantity(order);
    }

    private void selectProductInComboBox(String article) {
        if (article != null && !article.isEmpty()) {
            productComboBox.getItems().stream()
                    .filter(item -> item.startsWith(article))
                    .findFirst()
                    .ifPresent(productComboBox::setValue);
        }
    }

    private void loadProductQuantity(Order order) {
        int quantity = orderRepository.getProductQuantity(order.getOrderNumber(), order.getOrderArticle());
        quantityField.setText(String.valueOf(quantity > 0 ? quantity : 1));
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
            Order order = buildOrderFromForm();

            if (isEditMode) {
                updateExistingOrder(order);
            } else {
                createNewOrder(order);
            }

            service.setOrderToEdit(null);
            backToOrders();
        } catch (NumberFormatException e) {
            service.showAlert("Ошибка", "Проверьте правильность ввода: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Order buildOrderFromForm() {
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

        return new Order(orderNumber, orderArticle, status, orderDate, deliveryDate, pickUpAddress, clientName, code);
    }

    private void updateExistingOrder(Order order) {
        String productData = productComboBox.getValue();
        String productArticle = productData != null ? productData.split(" - ")[0] : "";
        int quantity = Integer.parseInt(quantityField.getText());
        String oldProductArticle = currentOrder != null ? currentOrder.getOrderArticle() : productArticle;

        orderRepository.updateOrder(order, productArticle, quantity, oldProductArticle);
        service.showAlert("Успех", "Заказ успешно обновлен!", Alert.AlertType.INFORMATION);
    }

    private void createNewOrder(Order order) {
        String productData = productComboBox.getValue();
        String productArticle = productData != null ? productData.split(" - ")[0] : "";
        int quantity = Integer.parseInt(quantityField.getText());

        orderRepository.addOrder(order, productArticle, quantity);
        service.showAlert("Успех", "Заказ успешно добавлен!", Alert.AlertType.INFORMATION);
    }

    private boolean validateFields() {
        if (orderArticleField.getText().trim().isEmpty()) {
            showValidationError("Введите артикул заказа");
            return false;
        }
        if (clientComboBox.getValue() == null) {
            showValidationError("Выберите клиента");
            return false;
        }
        if (pickUpPointComboBox.getValue() == null) {
            showValidationError("Выберите пункт выдачи");
            return false;
        }
        if (productComboBox.getValue() == null) {
            showValidationError("Выберите товар");
            return false;
        }
        if (quantityField.getText().isEmpty() || quantityField.getText().equals("0")) {
            showValidationError("Введите количество (больше 0)");
            return false;
        }
        if (codeField.getText().isEmpty()) {
            showValidationError("Введите код получения");
            return false;
        }
        return true;
    }

    private void showValidationError(String message) {
        service.showAlert("Ошибка", message, Alert.AlertType.WARNING);
    }

    private void backToOrders() {
        service.changeScene("/org/example/practica5/OrdersList.fxml", "Список заказов");
    }
}