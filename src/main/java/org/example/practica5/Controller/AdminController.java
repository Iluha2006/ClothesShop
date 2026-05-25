package org.example.practica5.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.*;
import org.example.practica5.Repository.*;
import org.example.practica5.Service;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML private TextField articleField;
    @FXML private ComboBox<String> productNameCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> providerCombo;
    @FXML private ComboBox<String> manufacturerCombo;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private ComboBox<String> unitCombo;
    @FXML private TextField priceField;
    @FXML private Spinner<Integer> discountSpinner;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imagePathField;
    @FXML private ImageView productImageView;


    private  ProductNameRepostory productNameRepostory;
    private ManufacturerRepository manufacturerRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProviderRepository providerRepository;
    private Service service;

    @FXML
    private void initialize() {
        DBHandler dbHandler = new DBHandler();
        productRepository = new ProductRepository(dbHandler);
        categoryRepository = new CategoryRepository(dbHandler);
        manufacturerRepository = new ManufacturerRepository(dbHandler);
        providerRepository = new ProviderRepository(dbHandler);
        productNameRepostory= new ProductNameRepostory(dbHandler);
        service = Service.getInstance();

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 1));
        discountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        loadComboBoxData();

        unitCombo.getItems().addAll("шт.");
        unitCombo.getSelectionModel().selectFirst();
    }

    private void loadComboBoxData() {

        categoryCombo.getItems().clear();
        for (Category c : categoryRepository.getAllCategories()) {
            categoryCombo.getItems().add(c.getName());
        }
        if (!categoryCombo.getItems().isEmpty()) {
            categoryCombo.getSelectionModel().selectFirst();
        }

        providerCombo.getItems().clear();
        for (Provider p : providerRepository.getAllProviders()) {
            providerCombo.getItems().add(p.getName());
        }
        if (!providerCombo.getItems().isEmpty()) {
            providerCombo.getSelectionModel().selectFirst();
        }

        for(ProductName productName : productNameRepostory.loadProductNames()){
            productNameCombo.getItems().add(productName.getName());
        }

        loadManufacturers();
    }

    private void loadManufacturers() {
        manufacturerCombo.getItems().clear();
        for (Manufacturer manufacturer : manufacturerRepository.getAllManufacturers()) {
            manufacturerCombo.getItems().add(manufacturer.getName());
        }
        if (!manufacturerCombo.getItems().isEmpty()) {
            manufacturerCombo.getSelectionModel().selectFirst();
        }
    }



    @FXML
    private void saveProduct() {
        if (!validateFields()) {
            return;
        }

        try {
            String selectedProductName = productNameCombo.getValue().trim();


            Product product = new Product(
                    articleField.getText(),
                    quantitySpinner.getValue(),
                    unitCombo.getValue(),
                    Double.parseDouble(priceField.getText()),
                    providerCombo.getValue(),
                    manufacturerCombo.getValue(),
                    categoryCombo.getValue(),
                    selectedProductName,
                    discountSpinner.getValue(),
                    descriptionArea.getText(),
                    imagePathField.getText()
            );

            productRepository.addProduct(product);
            service.showAlert("Успех", "Товар успешно добавлен!", Alert.AlertType.INFORMATION);
            service.changeScene("/org/example/practica5/ProductList.fxml", "Список товаров");
            clearForm();

        } catch (NumberFormatException e) {
            service.showAlert("Ошибка", "Проверьте правильность ввода цены", Alert.AlertType.ERROR);
        } catch (Exception e) {
            service.showAlert("Ошибка", "Ошибка при добавлении товара: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (articleField.getText().isEmpty()) {
            service.showAlert("Ошибка", "Введите артикул", Alert.AlertType.WARNING);
            return false;
        }
        if (productNameCombo.getValue() == null || productNameCombo.getValue().isEmpty()) {
            service.showAlert("Ошибка", "Введите или выберите название товара", Alert.AlertType.WARNING);
            return false;
        }
        if (priceField.getText().isEmpty()) {
            service.showAlert("Ошибка", "Введите цену", Alert.AlertType.WARNING);
            return false;
        }
        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            service.showAlert("Ошибка", "Цена должна быть числом", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void clearForm() {
        articleField.clear();
        productNameCombo.getSelectionModel().selectFirst();
        categoryCombo.getSelectionModel().selectFirst();
        providerCombo.getSelectionModel().selectFirst();
        manufacturerCombo.getSelectionModel().selectFirst();
        quantitySpinner.getValueFactory().setValue(1);
        unitCombo.getSelectionModel().selectFirst();
        priceField.clear();
        discountSpinner.getValueFactory().setValue(0);
        descriptionArea.clear();
        imagePathField.clear();
        productImageView.setImage(null);
    }

    @FXML
    private void cancel() {
        service.changeScene("/org/example/practica5/ProductList.fxml", "Список товаров");
    }

    @FXML
    private void chooseImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
            Image image = new Image(selectedFile.toURI().toString());
            productImageView.setImage(image);
        }
    }
}