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
    private final RecommendationRepository recommendationRepository; // JDBC
    private final ObjectMapper objectMapper;

    public DynamicRecommendationService(RuleRepository ruleRepository, RecommendationRepository recommendationRepository) {
        this.ruleRepository = ruleRepository;
        this.recommendationRepository = recommendationRepository;
        this.objectMapper = new ObjectMapper();
    }

    public List<RecommendationDTO> getDynamicRecommendations(UUID userId) {
        List<RecommendationDTO> result = new ArrayList<>();

        for (Rule rule : ruleRepository.findAll()) {
            if (evaluateRule(userId, rule.getRuleJson())) {
                // Получить информацию о продукте из другой БД
                String productName = recommendationRepository.getProductNameById(rule.getRecommendedProductId());
                String productDescription = recommendationRepository.getProductDescriptionById(rule.getRecommendedProductId());

                result.add(new RecommendationDTO(
                        rule.getRecommendedProductId().toString(),
                        productName,
                        productDescription
                ));
            }
        }

        return result;
    }

    private boolean evaluateRule(UUID userId, String ruleJson) {
        try {
            JsonNode ruleNode = objectMapper.readTree(ruleJson);
            for (JsonNode condition : ruleNode) {
                if (!evaluateCondition(userId, condition)) {
                    return false; // Все условия должны быть true
                }
            }
            return true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid rule JSON", e);
        }
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
            case "TRANSACTION_SUM_COMPARE" -> {
                String productType = args.get(0).asText();
                String transactionType = args.get(1).asText();
                String operator = args.get(2).asText();
                long value = args.get(3).asLong();
                long sum = recommendationRepository.getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, transactionType);
                yield compare(sum, operator, value);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                String productType = args.get(0).asText();
                String operator = args.get(1).asText();
                long depositSum = recommendationRepository.getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, "DEPOSIT");
                long withdrawSum = recommendationRepository.getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, "WITHDRAW");
                yield compare(depositSum, operator, withdrawSum);
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
