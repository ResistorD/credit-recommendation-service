package ru.skypro.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private String id;
    private String name;
    private String text;

    public RecommendationDTO(String id, String name, String text) {
    }
}
