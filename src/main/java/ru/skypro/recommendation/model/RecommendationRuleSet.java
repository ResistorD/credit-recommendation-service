package ru.skypro.recommendation.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> check(UUID userId);
}
