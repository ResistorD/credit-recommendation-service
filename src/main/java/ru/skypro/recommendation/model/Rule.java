package ru.skypro.recommendation.model;

import java.util.List;

public interface Rule {
    boolean matches(User user, List<Transaction> transactions, List<Product> Products);
}
