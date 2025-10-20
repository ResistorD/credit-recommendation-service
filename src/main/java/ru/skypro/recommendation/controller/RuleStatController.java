package ru.skypro.recommendation.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.recommendation.model.RuleStat;
import ru.skypro.recommendation.service.RuleStatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleStatController {

    private final RuleStatService service;

    public RuleStatController(RuleStatService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<RuleStat> stats = service.getAllStats();
        Map<String, Object> response = new HashMap<>();
        response.put("stats", stats);
        return response;
    }
}
