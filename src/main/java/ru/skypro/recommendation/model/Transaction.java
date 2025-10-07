package ru.skypro.recommendation.model;

public class Transaction {
    private String id;
    private String productId;
    private String userId;
    private String type;
    private Integer amount;

    // Конструкторы
    public Transaction() {}

    public Transaction(String id, String productId, String userId, String type, Integer amount) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
