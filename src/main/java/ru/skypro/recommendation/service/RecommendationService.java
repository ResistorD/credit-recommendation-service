package ru.skypro.recommendation.service;

import org.springframework.stereotype.Service;
import ru.skypro.recommendation.dto.RecommendationDTO;
import ru.skypro.recommendation.model.RecommendationRuleSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> staticRules; // старые фиксированные правила
    private final DynamicRecommendationService dynamicRules;

    public RecommendationService(List<RecommendationRuleSet> staticRules, DynamicRecommendationService dynamicRules) {
        this.staticRules = staticRules;
        this.dynamicRules = dynamicRules;
    }

    public List<RecommendationDTO> generateRecommendations(UUID userId) {
        List<RecommendationDTO> staticRecommendations = staticRules.stream()
                .map(rule -> rule.check(userId))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();

        List<RecommendationDTO> dynamicRecommendations = dynamicRules.getDynamicRecommendations(userId);

        staticRecommendations.addAll(dynamicRecommendations);

        return staticRecommendations;
    }
}
