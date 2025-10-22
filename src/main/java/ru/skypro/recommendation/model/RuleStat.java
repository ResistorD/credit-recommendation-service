package ru.skypro.recommendation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("rule_stats")  // таблица в БД
public class RuleStat {

    @Id
    private UUID id;       // id записи статистики
    private UUID ruleId;   // id правила
    private int count;     // счётчик срабатываний

    public RuleStat() {}

    public RuleStat(UUID id, UUID ruleId, int count) {
        this.id = id;
        this.ruleId = ruleId;
        this.count = count;
    }

    // геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
