package org.example.practica5.Model;

public class Product {
    private String article;
    private int quantity;
    private String unit;
    private double price;
    private String providerName;
    private String manufacturerName;
    private String categoryName;
    private String productName;
    private int currentDiscount;
    private String productDescription;
    private String imagePath;
    private double totalPrice;


    public Product(String article, int quantity, String unit, double price,
                   String providerName, String manufacturerName, String categoryName,
                   String productName, int currentDiscount,
                   String productDescription, String imagePath) {
        this.article = article;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.providerName = providerName;
        this.manufacturerName = manufacturerName;
        this.categoryName = categoryName;
        this.productName = productName;
        this.currentDiscount = currentDiscount;
        this.productDescription = productDescription;
        this.imagePath = imagePath;
    }

    public Product(String article) {
        this.article = article;
    }

    public Product(String article, String name) {
        this.article=article;
        this.productName=name;
    }

    public String getArticle() { return article; }
    public int getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public double getPrice() { return price; }
    public String getProviderName() { return providerName; }
    public String getManufacturerName() { return manufacturerName; }
    public String getCategoryName() { return categoryName; }
    public String getProductName() { return productName; }
    public int getCurrentDiscount() { return currentDiscount; }
    public String getProductDescription() { return productDescription; }
    public String getImagePath() { return imagePath; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public double calculateTotalPrice() {
        if (currentDiscount > 0) {
            this.totalPrice = getPrice() - (getPrice() / 100 * currentDiscount);
        } else {
            this.totalPrice = price;
        }
        return this.totalPrice;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}