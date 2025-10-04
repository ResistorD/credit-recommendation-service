package ru.skypro.recommendation.rules;

import org.springframework.stereotype.Component;
import ru.skypro.recommendation.model.RecommendationDTO;
import ru.skypro.recommendation.model.RecommendationRuleSet;
import ru.skypro.recommendation.repository.RecommendationRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class DebitInvestSavingsRule implements RecommendationRuleSet {
    private final RecommendationRepository repository;

    public DebitInvestSavingsRule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        // Проверки:
        // - есть ли продукт DEBIT
        // - нет ли продукта INVEST
        // - сумма пополнений по SAVING > 1000
        if (hasDebitProduct(userId) && !hasInvestProduct(userId) && getSavingsDepositSum(userId) > 1000) {
            return Optional.of(new RecommendationDTO(
                    "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!..."
            ));
        }
        return Optional.empty();
    }

    // вспомогательные методы
    private boolean hasDebitProduct(UUID userId) {
        return repository.hasProductOfTypeByUser(userId, "DEBIT");
    }

    private boolean hasInvestProduct(UUID userId) {
        return repository.hasProductOfTypeByUser(userId, "INVEST");
    }

    private long getSavingsDepositSum(UUID userId) {
        // Предполагаем, что "пополнение" — это транзакция с типом "DEPOSIT"
        return repository.getSumOfTransactionsByUserAndProductType(userId, "SAVING", "DEPOSIT");
    }
}
