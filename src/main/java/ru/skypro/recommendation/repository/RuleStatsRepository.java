package ru.skypro.recommendation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.recommendation.model.RuleStats;

import java.util.UUID;

@Repository
public interface RuleStatsRepository extends CrudRepository<RuleStats, UUID> {
}
