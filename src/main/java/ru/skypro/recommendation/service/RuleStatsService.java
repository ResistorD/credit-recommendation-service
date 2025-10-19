package ru.skypro.recommendation.service;

import org.springframework.stereotype.Service;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.model.RuleStats;
import ru.skypro.recommendation.repository.RuleRepository;
import ru.skypro.recommendation.repository.RuleStatsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RuleStatsService {
    private final RuleStatsRepository statsRepository;
    private final RuleRepository ruleRepository;

    public RuleStatsService(RuleStatsRepository statsRepository, RuleRepository ruleRepository) {
        this.statsRepository = statsRepository;
        this.ruleRepository = ruleRepository;
    }


    public void incrementHitCount(UUID ruleId) {
        Optional<RuleStats> existingStats = statsRepository.findById(ruleId);
        RuleStats stats;
        if (existingStats.isPresent()) {
            stats = existingStats.get();
            stats.increment();
        } else {
            stats = new RuleStats(ruleId, 1);
        }
        statsRepository.save(stats);
    }

    public List<RuleStatsEntry> getAllStats() {
        List<RuleStatsEntry> result = new ArrayList<>();
        // Получаем все существующие правила
        for (Rule rule : ruleRepository.findAll()) {
            UUID ruleId = rule.getId();
            RuleStats stats = statsRepository.findById(ruleId).orElse(new RuleStats(ruleId, 0));
            result.add(new RuleStatsEntry(stats.getRuleId().toString(), stats.getHitCount()));
        }
        return result;
    }

    public void deleteStatsForRule(UUID ruleId) {
        statsRepository.deleteById(ruleId);
    }

    // DTO для ответа
    public record RuleStatsEntry(String ruleId, long count) {}
}
