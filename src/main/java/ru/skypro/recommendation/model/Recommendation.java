package ru.skypro.recommendation.model;

import java.util.List;

public class Recommendation {
    private String id;
    private String name;
    private String type;
    private String description;
    private List<Rule> rules;

    // Конструктор
    public Recommendation(String id, String name, String type, String description, List<Rule> rules) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.rules = rules;
    }

    // Геттеры
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<Rule> getRules() {
        return rules;
    }
    //Сеттеры

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}

