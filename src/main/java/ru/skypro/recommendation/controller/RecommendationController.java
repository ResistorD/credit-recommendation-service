package ru.skypro.recommendation.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.recommendation.dto.RecommendationDTO;
import ru.skypro.recommendation.service.RecommendationService;

import java.util.*;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getRecommendation(@PathVariable UUID userId) {
        List<RecommendationDTO> recommendations = recommendationService.generateRecommendations(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("recommendations", recommendations);

        return response;
    }
}
