package ru.skypro.recommendation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.recommendation.repository.RecommendationRepository;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final RecommendationRepository recommendationRepository;

    public ManagementController(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @PostMapping("/clear-caches")
    public void clearCaches() {
        recommendationRepository.clearCaches();
    }
}