package ru.skypro.recommendation.service;

import org.springframework.stereotype.Service;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.repository.RuleRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class RuleService {

    private final RuleRepository repository;

    public RuleService(RuleRepository repository) {
        this.repository = repository;
    }

    public Iterable<Rule> getAll() {
        return repository.findAll();
    }

    public Optional<Rule> getById(UUID id) {
        return repository.findById(id);
    }

    public Rule create(Rule rule) {
        return repository.save(rule);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
