package ru.skypro.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.skypro.recommendation.dto.RecommendationDTO;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.repository.RecommendationRepository;
import ru.skypro.recommendation.repository.RuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DynamicRecommendationService {
    private final RuleRepository ruleRepository;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;
    private final RuleStatsService ruleStatsService;

    public DynamicRecommendationService(RuleRepository ruleRepository, RecommendationRepository recommendationRepository, RuleStatsService ruleStatsService) {
        this.ruleRepository = ruleRepository;
        this.recommendationRepository = recommendationRepository;
        this.objectMapper = new ObjectMapper();
        this.ruleStatsService = ruleStatsService;
    }

    public List<RecommendationDTO> getDynamicRecommendations(UUID userId) {
        List<RecommendationDTO> result = new ArrayList<>();

        for (Rule rule : ruleRepository.findAll()) {
            JsonNode ruleNode;
            try {
                ruleNode = objectMapper.readTree(rule.getRuleDescription());
            } catch (JsonProcessingException e) {
                // Логировать ошибку и пропустить правило
                continue;
            }

            JsonNode conditionsNode = ruleNode.get("conditions");
            JsonNode recommendedProductIdNode = ruleNode.get("recommendedProductId");

            if (conditionsNode == null) {
                // Старый формат — массив условий
                conditionsNode = ruleNode;
            }

            if (recommendedProductIdNode == null) {
                // Пропускаем правило, если нет recommendedProductId
                continue;
            }

            UUID recommendedProductId = UUID.fromString(recommendedProductIdNode.asText());

            if (evaluateRule(userId, conditionsNode)) {
                ruleStatsService.incrementHitCount(rule.getId());
                String productName = recommendationRepository.getProductNameById(recommendedProductId);
                String productDescription = recommendationRepository.getProductDescriptionById(recommendedProductId);

                result.add(new RecommendationDTO(
                        recommendedProductId.toString(),
                        productName,
                        productDescription));
            }
        }

        return result;
    }

    private boolean evaluateRule(UUID userId, JsonNode ruleNode) {
        for (JsonNode condition : ruleNode) {
            if (!evaluateCondition(userId, condition)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(UUID userId, JsonNode condition) {
        String query = condition.get("query").asText();
        var args = condition.get("arguments");
        boolean negate = condition.has("negate") && condition.get("negate").asBoolean();

        boolean result = switch (query) {
            case "USER_OF" -> {
                String productType = args.get(0).asText();
                yield recommendationRepository.hasProductOfTypeByUser(userId, productType);
            }
            case "ACTIVE_USER_OF" -> {
                String productType = args.get(0).asText();
                yield recommendationRepository.isActiveUserOfProductType(userId, productType);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                String productType = args.get(0).asText();
                String operator = args.get(1).asText();
                long depositSum = recommendationRepository.getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, "DEPOSIT");
                long withdrawSum = recommendationRepository.getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, "WITHDRAW");
                yield compare(depositSum, operator, withdrawSum);
            }
            case "TRANSACTION_SUM_COMPARE" -> {
                String productType = args.get(0).asText();
                String transactionType = args.get(1).asText();
                String operator = args.get(2).asText();
                long value = args.get(3).asLong();
                long sum = recommendationRepository.getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, transactionType);
                yield compare(sum, operator, value);
            }
            default -> false;
        };

        return negate ? !result : result;
    }

    private boolean compare(long sum, String operator, long value) {
        return switch (operator) {
            case ">" -> sum > value;
            case "<" -> sum < value;
            case "=" -> sum == value;
            case ">=" -> sum >= value;
            case "<=" -> sum <= value;
            default -> false;
        };
    }
}
