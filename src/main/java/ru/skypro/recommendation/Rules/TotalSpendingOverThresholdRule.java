package ru.skypro.recommendation.Rules;

import ru.skypro.recommendation.model.Product;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.Transaction;
import ru.skypro.recommendation.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TotalSpendingOverThresholdRule implements Rule {
    private final int threshold;

    public TotalSpendingOverThresholdRule(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean matches(User user, List<Transaction> transactions, List<Product> Products) {
        Set<String> debitProductIds = Products.stream()
                .filter(p -> "DEBIT".equals(p.getType()))
                .map(Product::getId)
                .collect(Collectors.toSet());

        int totalSpending = transactions.stream()
                .filter(t -> "WITHDRAW".equals(t.getType()) || "PAYMENT".equals(t.getType()))
                .filter(t -> debitProductIds.contains(t.getProductId()))
                .mapToInt(Transaction::getAmount)
                .sum();

        return totalSpending > threshold;
    }
}
