package ru.skypro.recommendation.service;

import org.springframework.stereotype.Service;
import ru.skypro.recommendation.model.RuleStat;
import ru.skypro.recommendation.repository.RuleStatRepository;

import java.util.List;
import java.util.UUID;

@Service
public class RuleStatService {

    private final RuleStatRepository repository;

    public RuleStatService(RuleStatRepository repository) {
        this.repository = repository;
    }

    public List<RuleStat> getAllStats() {
        return (List<RuleStat>) repository.findAll();
    }

    public void incrementRuleStat(UUID ruleId) {
        RuleStat stat = repository.findByRuleId(ruleId)
                .orElseGet(() -> new RuleStat(UUID.randomUUID(), ruleId, 0));
        stat.setCount(stat.getCount() + 1);
        repository.save(stat);
    }

    public void deleteByRule(UUID ruleId) {
        repository.findByRuleId(ruleId).ifPresent(repository::delete);
    }

    public void clearAllStats() {
        repository.deleteAll();
    }
}
