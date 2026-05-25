package org.example.practica5.Filter;

import org.example.practica5.Model.Product;
import org.example.practica5.Repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class FilterProduct {

    private ProductRepository productRepository;

    public FilterProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public List<Product> filterByCategory(List<Product> products, String category) {
        if (category == null || category.equals("Все категории") || category.isEmpty()) {
            return products;
        }
        return products.stream()
                .filter(p -> category.equals(p.getCategoryName()))
                .collect(Collectors.toList());
    }


    public List<Product> filterByProvider(List<Product> products, String provider) {
        if (provider == null || provider.equals("Все поставщики") || provider.isEmpty()) {
            return products;
        }
        return products.stream()
                .filter(p -> provider.equals(p.getProviderName()))
                .collect(Collectors.toList());
    }
    public List<Product> searchInList(List<Product> products, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return products;
        }
        String term = searchTerm.toLowerCase().trim();
        return products.stream()
                .filter(p ->
                        (p.getProductName() != null && p.getProductName().toLowerCase().contains(term)) ||
                                (p.getArticle() != null && p.getArticle().toLowerCase().contains(term)) ||
                                (p.getCategoryName() != null && p.getCategoryName().toLowerCase().contains(term)) ||
                                (p.getProviderName() != null && p.getProviderName().toLowerCase().contains(term)) ||
                                (p.getProductDescription() != null && p.getProductDescription().toLowerCase().contains(term)) ||


                                String.valueOf(p.getPrice()).contains(term) ||
                                String.valueOf(p.getQuantity()).contains(term)
                )
                .collect(Collectors.toList());
    }

}
