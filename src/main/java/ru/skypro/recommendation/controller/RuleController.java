package ru.skypro.recommendation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.service.RuleService;

import java.util.UUID;

@RestController
@RequestMapping("/rules")
public class RuleController {

    private final RuleService service;

    public RuleController(RuleService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<Rule> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getById(@PathVariable UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Rule create(@RequestBody Rule rule) {
        return service.create(rule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
