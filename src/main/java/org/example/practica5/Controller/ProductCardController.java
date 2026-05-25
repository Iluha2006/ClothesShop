package org.example.practica5.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.practica5.Model.Product;

import java.io.File;
import java.net.URL;

public class ProductCardController {

    @FXML private AnchorPane rootPane;
    @FXML private Label nameProduct;
    @FXML private Label descriptionProduct;
    @FXML private Label provider;
    @FXML private Label txtDiscount;
    @FXML private Text txtPrice;
    @FXML private Label txtTotalPrice;
    @FXML private Label txtQuantity;
    @FXML private ImageView imgCard;

    public void setProductCardData(Product product) {
        if (product == null) return;

        setProductInfo(product);
        setDiscountAndPrice(product);
        loadProductImage(product);
        applyCardBackgroundStyle(product);
        applyTextColorBasedOnBackground(product); // Добавляем метод для цвета текста
    }

    private void setProductInfo(Product product) {
        String productName = product.getProductName() != null ? product.getProductName() : "Без названия";
        nameProduct.setText(productName);

        String description = product.getProductDescription() != null ? product.getProductDescription() : "Нет описания";
        descriptionProduct.setText("Описание: " + description);

        String unit = product.getUnit() != null ? product.getUnit() : "шт.";
        txtQuantity.setText("В наличии: " + product.getQuantity() + " " + unit);

        String providerName = product.getProviderName() != null ? product.getProviderName() : "Не указан";
        provider.setText("Поставщик: " + providerName);
    }

    private void setDiscountAndPrice(Product product) {
        int discount = product.getCurrentDiscount();
        double finalPrice = product.calculateTotalPrice();

        txtPrice.setText(String.format("%.2f руб.", product.getPrice()));

        if (discount > 0) {
            txtDiscount.setText("-" + discount + "%");
            txtDiscount.setVisible(true);
            txtDiscount.setManaged(true);

            txtTotalPrice.setText("Итоговая цена: " + String.format("%.2f руб.", finalPrice));
            txtTotalPrice.setVisible(true);
            txtTotalPrice.setManaged(true);
            txtTotalPrice.setStyle("-fx-text-fill: red;");

            txtPrice.setFill(Color.RED);
            txtPrice.setStyle("-fx-strikethrough: true;");
        } else {
            txtDiscount.setVisible(false);
            txtDiscount.setManaged(false);
            txtTotalPrice.setVisible(false);
            txtTotalPrice.setManaged(false);
            txtPrice.setFill(Color.BLACK);
            txtPrice.setStyle("");
        }
    }

    private void loadProductImage(Product product) {
        String imagePath = product.getImagePath();
        try {
            Image image = new Image(imagePath, 120, 120, true, true);

            if (!image.isError()) {
                imgCard.setImage(image);
            } else {
                imgCard.setImage(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            imgCard.setImage(null);
        }
    }

    private void applyCardBackgroundStyle(Product product) {
        if (rootPane == null) return;

        int discount = product.getCurrentDiscount();
        int quantity = product.getQuantity();

        if (quantity == 0) {
            rootPane.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 8;");
        } else if (discount >= 15) {
            rootPane.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 8;");
        } else {
            rootPane.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        }
    }


    private void applyTextColorBasedOnBackground(Product product) {
        if (rootPane == null) return;

        int discount = product.getCurrentDiscount();
        int quantity = product.getQuantity();


        boolean isDarkBackground = (quantity == 0 || discount >= 15);

        if (isDarkBackground) {

            String whiteStyle = "-fx-text-fill: white;";
            nameProduct.setStyle(whiteStyle);
            descriptionProduct.setStyle(whiteStyle);
            provider.setStyle(whiteStyle);
            txtQuantity.setStyle(whiteStyle);


            if (discount > 0) {
                txtDiscount.setStyle("-fx-text-fill: red;");

                txtPrice.setStyle("-fx-fill: red; -fx-strikethrough: true;");
            } else {
                txtDiscount.setStyle(whiteStyle);
                txtPrice.setStyle("-fx-fill: white;");
            }
        } else {

            String blackStyle = "-fx-text-fill: black;";
            nameProduct.setStyle(blackStyle);
            descriptionProduct.setStyle(blackStyle);
            provider.setStyle(blackStyle);
            txtQuantity.setStyle(blackStyle);


            if (discount > 0) {
                txtDiscount.setStyle("-fx-text-fill: red;");
                txtPrice.setStyle("-fx-fill: red; -fx-strikethrough: true;");
            } else {
                txtDiscount.setStyle(blackStyle);

                txtPrice.setStyle("-fx-fill: black;");
            }
        }
    }
}