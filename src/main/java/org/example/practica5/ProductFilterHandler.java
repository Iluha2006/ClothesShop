package org.example.practica5;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.example.practica5.Model.Category;
import org.example.practica5.Model.Product;
import org.example.practica5.Model.Provider;
import org.example.practica5.Model.RoleUser;
import org.example.practica5.Repository.CategoryRepository;
import org.example.practica5.Repository.ProductRepository;
import org.example.practica5.Repository.ProviderRepository;
import org.example.practica5.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductFilterHandler {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;
    private final Service service;
    private final Consumer<List<Product>> onFilterApplied;

    private List<Product> allProducts = new ArrayList<>();

    public ProductFilterHandler(ProductRepository productRepository,
                                CategoryRepository categoryRepository,
                                ProviderRepository providerRepository,
                                Service service,
                                Consumer<List<Product>> onFilterApplied) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
        this.service = service;
        this.onFilterApplied = onFilterApplied;
    }

    // Загрузка всех продуктов
    public void loadProducts() {
        allProducts = productRepository.getAllProducts();
        if (onFilterApplied != null) {
            onFilterApplied.accept(new ArrayList<>(allProducts));
        }
    }

    // Загрузка категорий в комбобокс
    public void loadCategories(ComboBox<String> categoryFilter) {
        List<Category> categories = categoryRepository.getAllCategories();
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("Все категории");
        for (Category category : categories) {
            categoryFilter.getItems().add(category.getName());
        }
        categoryFilter.getSelectionModel().selectFirst();
    }

    // Загрузка поставщиков в комбобокс
    public void loadProviders(ComboBox<String> providerFilter) {
        List<Provider> providers = providerRepository.getAllProviders();
        providerFilter.getItems().clear();
        providerFilter.getItems().add("Все поставщики");
        for (Provider provider : providers) {
            providerFilter.getItems().add(provider.getName());
        }
        providerFilter.getSelectionModel().selectFirst();
    }

    // Проверка доступа к фильтрам
    public boolean hasFilterAccess() {
        return service.hasAccess(RoleUser.ADMIN) || service.hasAccess(RoleUser.MANAGER);
    }

    // Применение фильтров
    public void applyFilters(String selectedCategory, String selectedProvider) {
        List<Product> filtered = new ArrayList<>(allProducts);

        if (hasFilterAccess()) {
            if (selectedCategory != null && !selectedCategory.equals("Все категории")) {
                filtered = filtered.stream()
                        .filter(p -> selectedCategory.equals(p.getCategoryName()))
                        .toList();
            }
            if (selectedProvider != null && !selectedProvider.equals("Все поставщики")) {
                filtered = filtered.stream()
                        .filter(p -> selectedProvider.equals(p.getProviderName()))
                        .toList();
            }
        }

        if (onFilterApplied != null) {
            onFilterApplied.accept(filtered);
        }
    }


    public void searchProducts(String searchTerm, Consumer<List<Product>> onSearchComplete) {
        List<Product> results;
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            results = new ArrayList<>(allProducts);
        } else {
            String term = searchTerm.toLowerCase().trim();
            results = allProducts.stream()
                    .filter(p -> p.getProductName() != null &&
                            p.getProductName().toLowerCase().contains(term))
                    .toList();
        }
        if (onSearchComplete != null) {
            onSearchComplete.accept(results);
        }
    }

    public List<Product> getAllProducts() {
        return allProducts;
    }
}