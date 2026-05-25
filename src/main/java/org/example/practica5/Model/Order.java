package org.example.practica5.Model;

public class Order {
    private int orderNumber;
    private String orderArticle;
    private String orderStatus;
    private String orderDate;
    private String deliveryDate;
    private String pickUpAddress;
    private String clientName;
    private int codeToReceive;

    public Order(int orderNumber, String orderArticle, String orderStatus,
                 String orderDate, String deliveryDate, String pickUpAddress,
                 String clientName, int codeToReceive) {
        this.orderNumber = orderNumber;
        this.orderArticle = orderArticle;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.pickUpAddress = pickUpAddress;
        this.clientName = clientName;
        this.codeToReceive = codeToReceive;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getOrderArticle() {
        return orderArticle;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public int getCodeToReceive() {
        return codeToReceive;
    }
}
