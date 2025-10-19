package ru.skypro.recommendation.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import java.util.UUID;

@Entity
@Table("rule_stats")
public class RuleStats {
    @Id
    private UUID ruleId; // ссылка на Rule.id

    @Column("hit_count")
    private long hitCount = 0;

    // Конструкторы
    public RuleStats() {
    }

    public RuleStats(UUID ruleId, long hitCount) {
        this.ruleId = ruleId;
        this.hitCount = hitCount;
    }

    // Геттеры и сеттеры
    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public void increment() {
        this.hitCount++;
    }
}