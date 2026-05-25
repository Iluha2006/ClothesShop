package org.example.practica5.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.practica5.DB.DBHandler;
import org.example.practica5.Model.*;
import org.example.practica5.Repository.*;
import org.example.practica5.Service;

import java.io.File;

public class AddEditProductController {

    @FXML private Label formTitle;
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
    @FXML private Button saveButton;

    private  ProductNameRepostory productNameRepostory;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProviderRepository providerRepository;
    private ManufacturerRepository manufacturerRepository;
    private Service service;

    private boolean isEditMode = false;


    @FXML
    private void initialize() {
        DBHandler dbHandler = new DBHandler();
        productRepository = new ProductRepository(dbHandler);
        categoryRepository = new CategoryRepository(dbHandler);
        providerRepository = new ProviderRepository(dbHandler);
        manufacturerRepository = new ManufacturerRepository(dbHandler);

        productNameRepostory= new ProductNameRepostory(dbHandler);
        service = Service.getInstance();

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 1));
        discountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        loadComboBoxData();
        unitCombo.getItems().addAll("шт.");
        unitCombo.getSelectionModel().selectFirst();


        checkEditMode();
    }

    private void checkEditMode() {
        Product productToEdit = service.getProductToEdit();
        if (productToEdit != null) {
            isEditMode = true;
            productToEdit.getArticle();
            formTitle.setText("Редактирование товара");
            saveButton.setText("Обновить");
            loadProductData(productToEdit);
            articleField.setStyle("-fx-opacity: 0.6; -fx-background-color: #f0f0f0;");
        } 
    }

    private void loadProductData(Product product) {
        articleField.setText(product.getArticle());

        quantitySpinner.getValueFactory().setValue(product.getQuantity());
        priceField.setText(String.valueOf(product.getPrice()));
        discountSpinner.getValueFactory().setValue(product.getCurrentDiscount());
        descriptionArea.setText(product.getProductDescription());
        imagePathField.setText(product.getImagePath());


        if (product.getCategoryName() != null) {
            categoryCombo.getSelectionModel().select(product.getCategoryName());
        }
        if(product.getProductName() !=null){

            productNameCombo.getSelectionModel().select(product.getProductName());
        }
        if (product.getProviderName() != null) {
            providerCombo.getSelectionModel().select(product.getProviderName());
        }
        if (product.getManufacturerName() != null) {
            manufacturerCombo.getSelectionModel().select(product.getManufacturerName());
        }
        if (product.getUnit() != null) {
            unitCombo.getSelectionModel().select(product.getUnit());
        }


        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            try {
                Image image = new Image(product.getImagePath());
                productImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Не удалось загрузить изображение: " + e.getMessage());
            }
        }
    }

    private void loadComboBoxData() {
        for (Category c : categoryRepository.getAllCategories()) {
            categoryCombo.getItems().add(c.getName());
        }
        if (!categoryCombo.getItems().isEmpty()) {
            categoryCombo.getSelectionModel().selectFirst();
        }

        for (Provider p : providerRepository.getAllProviders()) {
            providerCombo.getItems().add(p.getName());
        }


        if (!providerCombo.getItems().isEmpty()) {
            providerCombo.getSelectionModel().selectFirst();
        }

        for(ProductName productName: productNameRepostory.loadProductNames()){
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
            Product product = getProduct(
                    articleField,
                    productNameCombo,
                    quantitySpinner,
                    unitCombo,
                    priceField,
                    providerCombo,
                    manufacturerCombo,
                    categoryCombo,
                    discountSpinner,
                    descriptionArea,
                    imagePathField);

            if (isEditMode) {

                productRepository.updateProduct(product);
                service.showAlert("Успех", "Товар успешно обновлен!", Alert.AlertType.INFORMATION);
            }
            service.changeScene("/org/example/practica5/ProductList.fxml", "Список товаров");


        } catch (NumberFormatException e) {
            service.showAlert("Ошибка", "Проверьте правильность ввода цены", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Product getProduct(TextField articleField, ComboBox<String> productNameField, Spinner<Integer> quantitySpinner, ComboBox<String> unitCombo, TextField priceField, ComboBox<String> providerCombo, ComboBox<String> manufacturerCombo, ComboBox<String> categoryCombo, Spinner<Integer> discountSpinner, TextArea descriptionArea, TextField imagePathField) {
        String article = articleField.getText().trim();

        Product product = new Product(
                article,
                quantitySpinner.getValue(),
                unitCombo.getValue(),
                Double.parseDouble(priceField.getText()),
                providerCombo.getValue(),
                manufacturerCombo.getValue(),
                categoryCombo.getValue(),
                productNameCombo.getValue(),
                discountSpinner.getValue(),
                descriptionArea.getText(),
                imagePathField.getText()
        );
        return product;
    }

    private boolean validateFields() {
        if (articleField.getText().isEmpty()) {
            service.showAlert("Ошибка", "Введите артикул", Alert.AlertType.WARNING);
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