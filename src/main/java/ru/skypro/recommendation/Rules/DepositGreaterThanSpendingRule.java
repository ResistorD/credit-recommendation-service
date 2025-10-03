package ru.skypro.recommendation.Rules;

import ru.skypro.recommendation.model.Product;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.Transaction;
import ru.skypro.recommendation.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DepositGreaterThanSpendingRule implements Rule {
    @Override
    public boolean matches(User user, List<Transaction> transactions, List<Product> Products) {
        Set<String> debitProductIds = Products.stream()
                .filter(p -> "DEBIT".equals(p.getType()))
                .map(Product::getId)
                .collect(Collectors.toSet());

        int totalDeposits = transactions.stream()
                .filter(t -> "DEPOSIT".equals(t.getType()))
                .filter(t -> debitProductIds.contains(t.getProductId()))
                .mapToInt(Transaction::getAmount)
                .sum();

        int totalSpending = transactions.stream()
                .filter(t -> "WITHDRAW".equals(t.getType()) || "PAYMENT".equals(t.getType())) // примеры трат
                .filter(t -> debitProductIds.contains(t.getProductId()))
                .mapToInt(Transaction::getAmount)
                .sum();

        return totalDeposits > totalSpending;
    }
}
