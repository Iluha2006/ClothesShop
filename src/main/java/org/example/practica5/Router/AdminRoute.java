package org.example.practica5.Router;

import org.example.practica5.Model.Product;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Repository.ProductRepository;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import org.example.practica5.Service;

public class AdminRoute {

    private Service service;
    private ProductRepository productRepository;


    public AdminRoute(ProductRepository productRepository) {
        this.service = Service.getInstance();
        this.productRepository = productRepository;
    }



    public void addProduct() {
        if (service.hasAccess(RoleUser.ADMIN)) {

                service.changeScene("/org/example/practica5/AddProduct.fxml",
                        "Добавление товара");


        }
    }

    public void editProduct(Product product) {
        if (service.hasAccess(RoleUser.ADMIN)) {
            if (product != null) {

                    service.setProductToEdit(product);
                    System.out.println("Передаем товар на редактирование: " + product.getProductName());
                    service.changeScene("EditProduct.fxml",
                            "Редактирование товара");

                    ;

            } else {
                service.showAlert("Ошибка", "Товар не выбран для редактирования", Alert.AlertType.WARNING);
            }
        }
    }

    public void deleteProduct(Product product) {
        if (service.hasAccess(RoleUser.ADMIN)) {
            if (product != null && product.getArticle() != null) {
                productRepository.deleteProduct(product.getArticle());
                service.showAlert("Подтверждение", "Товар удален", Alert.AlertType.INFORMATION);



            } else {
                service.showAlert("Ошибка", "Товар не выбран", Alert.AlertType.ERROR);
            }
        }
    }
}