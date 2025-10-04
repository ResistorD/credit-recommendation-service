package ru.skypro.recommendation.service;

import ru.skypro.recommendation.model.RecommendationDTO;
import ru.skypro.recommendation.model.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {
    private final List<RecommendationRuleSet> rules;

    @Autowired
    public RecommendationService(List<RecommendationRuleSet> rules) {
        this.rules = rules;
    }

    public List<RecommendationDTO> getRecommendations(UUID userId) {
        return rules.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
