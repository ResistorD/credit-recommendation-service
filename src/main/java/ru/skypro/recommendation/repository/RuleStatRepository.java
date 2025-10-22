package ru.skypro.recommendation.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.recommendation.model.RuleStat;

import java.util.Optional;
import java.util.UUID;

public interface RuleStatRepository extends CrudRepository<RuleStat, UUID> {
    Optional<RuleStat> findByRuleId(UUID ruleId); // Spring Data автоматически реализует
}
