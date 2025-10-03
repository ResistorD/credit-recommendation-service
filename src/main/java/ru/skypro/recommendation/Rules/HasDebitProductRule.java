package ru.skypro.recommendation.Rules;

import ru.skypro.recommendation.model.Product;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.Transaction;
import ru.skypro.recommendation.model.User;

import java.util.List;

public class HasDebitProductRule implements Rule {
    @Override
    public boolean matches(User user, List<Transaction> transactions, List<Product> Products) {
        return Products.stream()
                .anyMatch(p -> "DEBIT".equals(p.getType()));
    }
}
