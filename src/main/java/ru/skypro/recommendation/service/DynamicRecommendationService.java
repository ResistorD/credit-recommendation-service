package ru.skypro.recommendation.service;

import org.springframework.stereotype.Service;
import ru.skypro.recommendation.dto.RecommendationDTO;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.RuleStat;
import ru.skypro.recommendation.repository.RuleRepository;
import ru.skypro.recommendation.repository.RuleStatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DynamicRecommendationService {

    private final RuleRepository ruleRepository;
    private final RuleStatRepository ruleStatRepository;

    public DynamicRecommendationService(RuleRepository ruleRepository,
                                        RuleStatRepository ruleStatRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleStatRepository = ruleStatRepository;
    }

    public List<RecommendationDTO> getDynamicRecommendations(UUID userId) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (Rule rule : ruleRepository.findAll()) {
            // Проверка условия правила (здесь пока простой пример)
            if (applies(rule, userId)) {
                // Генерация рекомендации
                recommendations.add(new RecommendationDTO(rule.getRecommendedProductId(),
                        "Рекомендация по правилу: ",
                        "Рекомендация по правилу: " + rule.getRuleName()));

                // Увеличиваем счётчик срабатываний
                incrementRuleStat(rule.getId());
            }
        }

        return recommendations;
    }

    private boolean applies(Rule rule, UUID userId) {
        // TODO: сюда вставляем реальную логику динамического правила
        // Пока возвращаем true для демонстрации
        return true;
    }

    private void incrementRuleStat(UUID ruleId) {
        RuleStat stat = ruleStatRepository.findById(ruleId)
                .orElse(new RuleStat(UUID.randomUUID(), ruleId, 0));
        stat.setCount(stat.getCount() + 1);
        ruleStatRepository.save(stat);
    }
}
