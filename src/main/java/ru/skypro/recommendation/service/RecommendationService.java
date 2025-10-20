package ru.skypro.recommendation.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.skypro.recommendation.dto.RecommendationDTO;
import ru.skypro.recommendation.model.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> staticRules;
    private final DynamicRecommendationService dynamicRules;
    private final JdbcTemplate jdbcTemplate;

    public RecommendationService(List<RecommendationRuleSet> staticRules,
                                 DynamicRecommendationService dynamicRules,
                                 JdbcTemplate jdbcTemplate) {
        this.staticRules = staticRules;
        this.dynamicRules = dynamicRules;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Генерация рекомендаций для пользователя по его UUID
     */
    public List<RecommendationDTO> generateRecommendations(UUID userId) {
        List<RecommendationDTO> staticRecommendations = staticRules.stream()
                .map(rule -> rule.check(userId))
                .flatMap(Optional::stream)
                .toList();

        List<RecommendationDTO> dynamicRecommendations = dynamicRules.getDynamicRecommendations(userId);

        staticRecommendations.addAll(dynamicRecommendations);

        return staticRecommendations;
    }

    /**
     * Получение текстовых рекомендаций по имени пользователя для Telegram
     */
    public String getRecommendationsText(String username) {
        // 1. Ищем пользователя по имени
        List<UUID> userIds = jdbcTemplate.query(
                "SELECT id FROM users WHERE LOWER(name) = LOWER(?)",
                (rs, rowNum) -> UUID.fromString(rs.getString("id")),
                username
        );

        if (userIds.size() != 1) {
            return "Пользователь не найден.";
        }

        UUID userId = userIds.get(0);

        // 2. Генерируем рекомендации
        List<RecommendationDTO> recommendations = generateRecommendations(userId);

        if (recommendations.isEmpty()) {
            return "Для вас пока нет новых рекомендаций.";
        }

        // 3. Формируем текст для Telegram
        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте, ").append(username).append("!\n\n");
        sb.append("Новые продукты для вас:\n");

        for (RecommendationDTO rec : recommendations) {
            sb.append("• ").append(rec.getName()).append(": ").append(rec.getText()).append("\n");
        }

        return sb.toString();
    }
}
