package ru.skypro.recommendation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.repository.RuleRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final RuleStatsService ruleStatsService; // <- plural

    public RuleService(RuleRepository ruleRepository,
                       RuleStatsService ruleStatsService) {
        this.ruleRepository = ruleRepository;
        this.ruleStatsService = ruleStatsService;
    }

    public Iterable<Rule> getAll() {
        return ruleRepository.findAll();
    }

    public Optional<Rule> getById(UUID id) {
        return ruleRepository.findById(id);
    }

    public Rule create(Rule rule) {
        return ruleRepository.save(rule);
    }

    @Transactional
    public void deleteById(UUID id) {
        // очистка статистики перед удалением правила
        ruleStatsService.deleteStatsForRule(id);
        ruleRepository.deleteById(id);
    }

    // алиас на случай, если контроллер зовёт delete(id)
    public void delete(UUID id) {
        deleteById(id);
    }
}
