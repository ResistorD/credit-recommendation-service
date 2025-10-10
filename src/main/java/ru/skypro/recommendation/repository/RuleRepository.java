package ru.skypro.recommendation.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.recommendation.model.Rule;

import java.util.UUID;

public interface RuleRepository extends CrudRepository<Rule, UUID> {
}
