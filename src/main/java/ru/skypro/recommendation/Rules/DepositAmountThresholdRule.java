package ru.skypro.recommendation.Rules;

import ru.skypro.recommendation.model.Product;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.Transaction;
import ru.skypro.recommendation.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DepositAmountThresholdRule implements Rule {
    private final int threshold;

    public DepositAmountThresholdRule(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean matches(User user, List<Transaction> transactions, List<Product> Products) {
        Set<String> debitProductIds = Products.stream()
                .filter(p -> "DEBIT".equals(p.getType()))
                .map(Product::getId)
                .collect(Collectors.toSet());

        Set<String> savingProductIds = Products.stream()
                .filter(p -> "SAVING".equals(p.getType()))
                .map(Product::getId)
                .collect(Collectors.toSet());

        int totalDebitDeposits = transactions.stream()
                .filter(t -> "DEPOSIT".equals(t.getType()))
                .filter(t -> debitProductIds.contains(t.getProductId()))
                .mapToInt(Transaction::getAmount)
                .sum();

        int totalSavingDeposits = transactions.stream()
                .filter(t -> "DEPOSIT".equals(t.getType()))
                .filter(t -> savingProductIds.contains(t.getProductId()))
                .mapToInt(Transaction::getAmount)
                .sum();

        return totalDebitDeposits >= threshold || totalSavingDeposits >= threshold;
    }
}
