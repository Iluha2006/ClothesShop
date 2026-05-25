package org.example.practica5.Filter;

import javafx.scene.control.ComboBox;
import org.example.practica5.Filter.FilterProduct;
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

public class FilterHandler {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;
    private final FilterProduct filterProduct;
    private final Service service;


    public FilterHandler(ProductRepository productRepository,
                         CategoryRepository categoryRepository,
                         ProviderRepository providerRepository,
                         Service service) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
        this.filterProduct = new FilterProduct(productRepository);
        this.service = service;
    }




    public void loadCategories(ComboBox<String> categoryFilter) {
        List<Category> categories = categoryRepository.getAllCategories();
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("Все категории");
        for (Category category : categories) {
            categoryFilter.getItems().add(category.getName());
        }
        categoryFilter.getSelectionModel().selectFirst();
    }

    public void loadProviders(ComboBox<String> providerFilter) {
        List<Provider> providers = providerRepository.getAllProviders();
        providerFilter.getItems().clear();
        providerFilter.getItems().add("Все поставщики");
        for (Provider provider : providers) {
            providerFilter.getItems().add(provider.getName());
        }
        providerFilter.getSelectionModel().selectFirst();
    }

    public List<Product> applyFiltersCategory(List<Product> products, String selectedCategory) {
       return  filterProduct.filterByCategory(products,selectedCategory);
    }
    public  List<Product > FilterProvaider(List<Product> products ,String provaider){
        return  filterProduct.filterByProvider(products,provaider);

    }

    public List<Product> searchProducts(List<Product> products, String searchTerm) {
        return filterProduct.searchInList(products, searchTerm);
    }


}