package ru.skypro.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.skypro.recommendation.dto.RecommendationDTO;
import ru.skypro.recommendation.model.Rule;
import ru.skypro.recommendation.repository.RecommendationRepository;
import ru.skypro.recommendation.repository.RuleRepository;
import ru.skypro.recommendation.repository.RuleStatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DynamicRecommendationService {

    private final RuleRepository ruleRepository;
    private final RuleStatRepository ruleStatRepository; // ← добавлено
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DynamicRecommendationService(RuleRepository ruleRepository,
                                        RuleStatRepository ruleStatRepository, // ← добавлено
                                        RecommendationRepository recommendationRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleStatRepository = ruleStatRepository; // ← добавлено
        this.recommendationRepository = recommendationRepository;
    }

    public List<RecommendationDTO> getDynamicRecommendations(UUID userId) {
        List<RecommendationDTO> result = new ArrayList<>();

        for (Rule rule : ruleRepository.findAll()) {
            String json = rule.getRuleDescription();

            if (!evaluateRule(userId, json)) continue;

            UUID recId = extractRecommendedProductId(json);
            if (recId == null) continue;

            String name = recommendationRepository.getProductNameById(recId);
            String description = recommendationRepository.getProductDescriptionById(recId);

            result.add(new RecommendationDTO(recId, name, description));
        }
        return result;
    }

    private UUID extractRecommendedProductId(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode idNode = root.isObject() ? root.get("recommendedProductId") : null;
            if (idNode != null && !idNode.isNull()) {
                return UUID.fromString(idNode.asText());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean evaluateRule(UUID userId, String ruleJson) {
        try {
            JsonNode root = objectMapper.readTree(ruleJson);
            JsonNode conditions = root.isArray()
                    ? root
                    : (root.has("conditions") ? root.get("conditions") : null);

            if (conditions == null) return true;

            for (JsonNode condition : conditions) {
                if (!evaluateCondition(userId, condition)) return false;
            }
            return true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid rule JSON", e);
        }
    }

    private boolean evaluateCondition(UUID userId, JsonNode node) {
        String query = node.has("query") ? node.get("query").asText() : "";
        JsonNode args = node.get("arguments");

        switch (query) {
            case "USER_OF": {
                String productType = args.get(0).asText();
                return recommendationRepository.hasProductOfTypeByUser(userId, productType);
            }
            case "ACTIVE_USER_OF": {
                String productType = args.get(0).asText();
                return recommendationRepository.isActiveUserOfProductType(userId, productType);
            }
            case "TRANSACTION_SUM_COMPARE": {
                String productType = args.get(0).asText();
                String txType = args.get(1).asText();
                String cmp = args.get(2).asText();
                long amount = args.get(3).asLong();

                long sum = recommendationRepository
                        .getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, txType);
                return compare(sum, cmp, amount);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW": {
                String productType = args.get(0).asText();
                String direction = args.get(1).asText();
                String cmp = args.get(2).asText();
                long amount = args.get(3).asLong();

                long sum = recommendationRepository
                        .getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, direction);
                return compare(sum, cmp, amount);
            }
            default:
                return false;
        }
    }

    private boolean compare(long lhs, String cmp, long rhs) {
        switch (cmp) {
            case ">":
                return lhs > rhs;
            case ">=":
                return lhs >= rhs;
            case "<":
                return lhs < rhs;
            case "<=":
                return lhs <= rhs;
            case "==":
                return lhs == rhs;
            default:
                return lhs != rhs; // "!="
        }
    }
}
