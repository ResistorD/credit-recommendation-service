package ru.skypro.recommendation.Rules;

import ru.skypro.recommendation.model.Product;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.Transaction;
import ru.skypro.recommendation.model.User;

import java.util.List;

public class DoesNotHaveInvestProductRule implements Rule {
    @Override
    public boolean matches(User user, List<Transaction> transactions, List<Product> Products) {
        return Products.stream()
                .noneMatch(p -> "INVEST".equals(p.getType()));
    }
}
