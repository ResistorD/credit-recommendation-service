package ru.skypro.recommendation.model;

import ru.skypro.recommendation.dto.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDTO> check(UUID userId);
}
