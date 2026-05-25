package org.example.practica5.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.Order;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Model.User;
import org.example.practica5.Repository.OrderRepository;
import org.example.practica5.Router.OrderRoutes;
import org.example.practica5.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrdersListController {

    @FXML private ListView<Node> ordersListView;
    @FXML private Button btnBackProducts;
    @FXML private Button btnAddOrder;
    @FXML private Button btnEditOrder;
    @FXML private Button btnDeleteOrder;
    @FXML private Label RoleUser;
    @FXML private Label NameUser;

    private Service service;
    private OrderRepository orderRepository;
    private OrderRoutes orderRoutes;
    private List<Order> ordersList = new ArrayList<>();

    @FXML
    private void initialize() {
        service = Service.getInstance();
        DBHandler dbHandler = new DBHandler();
        orderRepository = new OrderRepository(dbHandler);
        orderRoutes = new OrderRoutes(orderRepository);

        loadUser();
        setupAccess();
        loadOrders();
        setupListViewDoubleClick();


        btnAddOrder.setOnAction(e -> orderRoutes.addOrder());
        btnBackProducts.setOnAction(e -> btnBackProductsClick());
        btnEditOrder.setOnAction(e -> editSelectedOrder());
        btnDeleteOrder.setOnAction(e -> deleteSelectedOrder());
    }

    private void setupAccess() {


        if(service.hasAccess(org.example.practica5.Model.RoleUser.ADMIN)){
            btnAddOrder.setVisible(true);
            btnEditOrder.setVisible(true);
            btnDeleteOrder.setVisible(true);

        }
        else {

            btnAddOrder.setVisible(false);
            btnEditOrder.setVisible(false);
            btnDeleteOrder.setVisible(false);
        }


    }

    private void setupListViewDoubleClick() {
        ordersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                editSelectedOrder();
            }
        });
    }

    private void editSelectedOrder() {
        int selectedIndex = ordersListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < ordersList.size()) {
            Order selectedOrder = ordersList.get(selectedIndex);
            orderRoutes.editOrder(selectedOrder);
        } else {
            service.showAlert("Ошибка", "Выберите заказ для редактирования", javafx.scene.control.Alert.AlertType.WARNING);
        }
    }

    private void deleteSelectedOrder() {
        int selectedIndex = ordersListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < ordersList.size()) {
            Order selectedOrder = ordersList.get(selectedIndex);

                    orderRoutes.deleteOrder(selectedOrder);
                    loadOrders();

        } else {
            service.showAlert("Ошибка", "Выберите заказ для удаления", javafx.scene.control.Alert.AlertType.WARNING);
        }
    }

    private void loadOrders() {
        ordersList = orderRepository.getAllOrders();
        loadOrdersInListView(ordersList);
    }

    private void loadOrdersInListView(List<Order> orders) {
        try {
            ordersListView.getItems().clear();
            for (Order order : orders) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/practica5/OrderCard.fxml"));
                Node root = loader.load();
                OrderCardController controller = loader.getController();
                controller.setOrderCardData(order);
                ordersListView.getItems().add(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            service.showAlert("Ошибка", "Не удалось загрузить карточки заказов", javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnBackProductsClick() {
        service.changeScene("/org/example/practica5/ProductList.fxml", "Список товаров");
    }

    private void loadUser() {
        User user = service.getCurrentUser();
        if (user != null) {
            NameUser.setText("Имя: " + user.getFullName());
            RoleUser.setText("Роль: " + user.getRole().getRole());
        }
    }
}