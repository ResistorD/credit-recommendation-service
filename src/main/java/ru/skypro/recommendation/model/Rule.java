package ru.skypro.recommendation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.util.UUID;

@Table("rules")
public class Rule {
    @Id
    private UUID id;

    @Column("rule_name")
    private String ruleName;

    @Column("rule_description")
    private String ruleDescription;

    // Конструкторы
    public Rule() {
    }

    public Rule(UUID id, String ruleName, String ruleDescription) {
        this.id = id;
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
}
