package ru.skypro.recommendation.model;

import java.util.List;

public class RecommendationDTO {
    private String id;
    private String name;
    private String text;

    public RecommendationDTO(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    // Геттеры и Сеттеры

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

