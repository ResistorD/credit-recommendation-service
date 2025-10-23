package ru.skypro.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.recommendation.service.RuleStatsService;

import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService ruleStatsService;

    public RuleStatsController(RuleStatsService ruleStatsService) {
        this.ruleStatsService = ruleStatsService;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("stats", ruleStatsService.getAllStats());
        return response;
    }
}
