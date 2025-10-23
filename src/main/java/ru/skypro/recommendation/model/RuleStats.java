package ru.skypro.recommendation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("rule_stats")
public class RuleStats {

    @Id
    private UUID ruleId;

    @Column("hit_count")
    private long hitCount;

    // ---- конструкторы ----
    public RuleStats() { }

    public RuleStats(UUID ruleId, long hitCount) {
        this.ruleId = ruleId;
        this.hitCount = hitCount;
    }

    // ---- геттеры/сеттеры ----
    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }

    public long getHitCount() { return hitCount; }
    public void setHitCount(long hitCount) { this.hitCount = hitCount; }

    // ---- доменная логика ----
    public void increment() { this.hitCount++; }
}
