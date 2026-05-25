package org.example.practica5.Handler;

import javafx.scene.control.ComboBox;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Service;

import java.util.Comparator;
import java.util.List;

public class SortHandler {

    private final Service service;

    public SortHandler(Service service) {
        this.service = service;
    }

    public boolean Sort() {
        return service.hasAccess(RoleUser.ADMIN) || service.hasAccess(RoleUser.MANAGER);
    }

    public void setupSorting(ComboBox<String> sortBy) {
        if (Sort()) {
            sortBy.setVisible(true);
            sortBy.getItems().clear();
            sortBy.getItems().addAll(
                    "Без сортировки",
                    "По возрастанию количества товара",
                    "По убыванию количества товара"
            );
            sortBy.getSelectionModel().selectFirst();
        } else {
            sortBy.setVisible(false);
        }
    }

    public void sortProducts(List<Product> products, ComboBox<String> sortBy) {
        String selectedOption = sortBy.getSelectionModel().getSelectedItem();
        if (selectedOption == null || selectedOption.equals("Без сортировки")) return;

        Comparator<Product> comparator;
        switch (selectedOption) {
            case "По возрастанию количества товара":
                comparator = Comparator.comparing(Product::getQuantity);
                break;
            case "По убыванию количества товара":
                comparator = Comparator.comparing(Product::getQuantity).reversed();
                break;
            default:
                return;
        }
        products.sort(comparator);
    }
}