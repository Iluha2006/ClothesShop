package org.example.practica5.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Filter.FilterHandler;
import org.example.practica5.Handler.SortHandler;
import org.example.practica5.Model.*;
import org.example.practica5.Repository.CategoryRepository;
import org.example.practica5.Repository.ProductRepository;
import org.example.practica5.Repository.ProviderRepository;
import org.example.practica5.Router.AdminRoute;
import org.example.practica5.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button logoutButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> providerFilter;
    @FXML private ComboBox<String> sortBy;
    @FXML private Button applyFilterButton;
    @FXML private HBox adminPanel;
    @FXML private Button addProductButton;
    @FXML private Button editProductButton;
    @FXML private Button deleteProductButton;
    @FXML private ListView<Node> productsListView;
    @FXML private Button btnOrders;
    private AdminRoute adminRoute;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProviderRepository providerRepository;
    private Service service;

    private List<Product> allProducts = new ArrayList<>();
    private List<Product> currentDisplayedProducts = new ArrayList<>();
    private FilterHandler filterHandler;

    private SortHandler sortHandler;

    @FXML
    private void initialize() {
        try {
            DBHandler dbHandler = new DBHandler();
            productRepository = new ProductRepository(dbHandler);
            categoryRepository = new CategoryRepository(dbHandler);
            providerRepository = new ProviderRepository(dbHandler);
            service = Service.getInstance();
            adminRoute = new AdminRoute(productRepository);
            filterHandler = new FilterHandler(productRepository, categoryRepository, providerRepository, service);
            sortHandler = new SortHandler(service);
            loadUserInfo();
            setupPanel();
            setupFilters();
            setupSorting();
            loadProducts();
            setupDynamicFilters();
            setupListViewDoubleClick();

            if (service.hasAccess(RoleUser.ADMIN) || service.hasAccess(RoleUser.MANAGER)) {
                btnOrders.setVisible(true);
                btnOrders.setManaged(true);
                btnOrders.setOnAction(e -> openOrdersList());
            } else {
                btnOrders.setVisible(false);
                btnOrders.setManaged(false);
            }
            System.out.println("Товаров загружено: " + (allProducts != null ? allProducts.size() : 0));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка в initialize: " + e.getMessage());
        }
    }

    private void openOrdersList() {

            service.changeScene("OrdersList.fxml", "Список заказов");


    }
    private void loadProducts() {
        allProducts = productRepository.getAllProducts();
        try {
            updateListView(allProducts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void refreshProducts() {
        System.out.println("Обновление списка товаров");
        allProducts = productRepository.getAllProducts();
        try {
            applyFiltersAndUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupListViewDoubleClick() {
        productsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                EditButtonAdmin();
            }
        });
    }

    private void setupDynamicFilters() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                applyFiltersAndUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        categoryFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                applyFiltersAndUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        providerFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                applyFiltersAndUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sortBy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                applyFiltersAndUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        applyFilterButton.setVisible(false);
        applyFilterButton.setManaged(false);
        searchButton.setVisible(false);
        searchButton.setManaged(false);
    }

    private void applyFiltersAndUpdate() throws IOException {
        List<Product> filtered = new ArrayList<>(allProducts);
        currentDisplayedProducts = new ArrayList<>(allProducts);

        String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();
        if (selectedCategory != null && !selectedCategory.equals("Все категории")) {
            filtered = filterHandler.applyFiltersCategory(filtered, selectedCategory);
            currentDisplayedProducts = filterHandler.applyFiltersCategory(currentDisplayedProducts, selectedCategory);
        }

        String selectedProvider = providerFilter.getSelectionModel().getSelectedItem();
        if (selectedProvider != null && !selectedProvider.equals("Все поставщики")) {
            filtered = filterHandler.FilterProvaider(filtered, selectedProvider);
            currentDisplayedProducts = filterHandler.FilterProvaider(currentDisplayedProducts, selectedProvider);
        }

        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            filtered = filterHandler.searchProducts(filtered, searchTerm);
            currentDisplayedProducts = filterHandler.searchProducts(currentDisplayedProducts, searchTerm);
        }

        sortHandler.sortProducts(filtered, sortBy);
        sortHandler.sortProducts(currentDisplayedProducts, sortBy);

        updateListView(filtered);
    }

    private void updateListView(List<Product> products) throws IOException {
        productsListView.getItems().clear();
        for (Product p : products) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/practica5/ProductCard.fxml"));
            Node root = fxmlLoader.load();
            ProductCardController controller = fxmlLoader.getController();
            controller.setProductCardData(p);
            productsListView.getItems().add(root);
        }
    }

    private void setupFilters() {
        if (service.hasAccess(RoleUser.MANAGER) || service.hasAccess(RoleUser.ADMIN)) {
            filterHandler.loadCategories(categoryFilter);
            filterHandler.loadProviders(providerFilter);
            categoryFilter.setVisible(true);
            providerFilter.setVisible(true);
        } else {
            categoryFilter.setVisible(false);
            providerFilter.setVisible(false);
        }
    }

    private void setupSorting() {
        sortHandler.setupSorting(sortBy);
    }

    public void setupPanel() {
        System.out.println("Setup panel called. User role: " + service.getCurrentUser().getRole());



        if (service.hasAccess(RoleUser.ADMIN)) {
            System.out.println("Admin detected - showing admin panel");
            adminPanel.setVisible(true);
            adminPanel.setManaged(true);

            addProductButton.setVisible(true);
            addProductButton.setManaged(true);

            editProductButton.setVisible(true);
            editProductButton.setManaged(true);

            deleteProductButton.setVisible(true);
            deleteProductButton.setManaged(true);

            addProductButton.setOnAction(e -> AddProdAdmin());
            editProductButton.setOnAction(e -> EditButtonAdmin());
            deleteProductButton.setOnAction(e -> DeleteButtonAdmin());

            logoutButton.setVisible(true);
            logoutButton.setManaged(true);
        } else {
            System.out.println("Not admin - hiding admin panel");
            adminPanel.setVisible(false);
            adminPanel.setManaged(false);
            logoutButton.setVisible(true);
            logoutButton.setManaged(true);
        }
    }

    public void EditButtonAdmin() {
        if (service.hasAccess(RoleUser.ADMIN)) {
            int selectedIndex = productsListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < allProducts.size()) {
                Product selectedProduct = allProducts.get(selectedIndex);
                adminRoute.editProduct(selectedProduct);
            } else {
                service.showAlert("Ошибка", "Выберите товар для редактирования", Alert.AlertType.WARNING);
            }
        }
    }

    public void AddProdAdmin() {
        if (service.hasAccess(RoleUser.ADMIN)) {
            adminRoute.addProduct();
        }
    }

    public void DeleteButtonAdmin() {
        if (service.hasAccess(RoleUser.ADMIN)) {
            int selectedIndex = productsListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < allProducts.size()) {
                Product selectedProduct = allProducts.get(selectedIndex);
                adminRoute.deleteProduct(selectedProduct);
                refreshProducts();
                service.showAlert("Удалениe", "Успешно", Alert.AlertType.WARNING);
            } else {
                service.showAlert("Ошибка", "Выберите товар для удаления", Alert.AlertType.WARNING);
            }
        }
    }

    @FXML
    private void handleSearch() {
        try {
            applyFiltersAndUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserInfo() {
        User currentUser = service.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText("Пользователь: " + currentUser.getFullName());

            RoleUser role = currentUser.getRole();
            if (role != null) {
                userRoleLabel.setText("Роль: " + currentUser.getRole().getRole());
            } else {
                userRoleLabel.setText("Роль: Не определена");
            }
        } else {
            userNameLabel.setText("Пользователь: Гость");
            userRoleLabel.setText("Роль: Гость");
        }
    }
    @FXML
    private void handleLogout() {
        service.changeScene("/org/example/practica5/SignIn.fxml", "Авторизация");
    }
}