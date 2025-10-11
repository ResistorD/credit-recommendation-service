package ru.skypro.recommendation.repository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean hasProductOfTypeByUser(UUID userId, String productType) {
        CacheKey key = new CacheKey(userId, productType);
        return productTypeCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) > 0
                    FROM TRANSACTIONS t
                    JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                    WHERE t.USER_ID = ? AND p.TYPE = ?
                    """;
            return jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
        });
    }

    public boolean isActiveUserOfProductType(UUID userId, String productType) {
        CacheKey key = new CacheKey(userId, productType);
        return activeUserCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) >= 5
                    FROM TRANSACTIONS t
                    JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                    WHERE t.USER_ID = ? AND p.TYPE = ?
                    """;
            return jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
        });
    }

    public long getSumOfTransactionsByUserAndProductType(UUID userId, String productType, String transactionType) {
        CacheKey key = new CacheKey(userId, productType);
        return activeUserCache.get(key, k -> {
            String sql = """
                    SELECT SUM(t.AMOUNT)
                    FROM TRANSACTIONS t
                    JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                    WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?
                    """;
            Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);
            return sum != null ? sum : 0;
        });
    }
    public long getSumOfTransactionsByUserAndProductAndTransactionType(UUID userId, String productType, String transactionType) {
        CacheKey3 key = new CacheKey3(userId, productType, transactionType);
        return transactionSumCache.get(key, k -> {
            String sql = """
                    SELECT SUM(t.AMOUNT)
                    FROM TRANSACTIONS t
                    JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                    WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?
                    """;
            Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);
            return sum != null ? sum : 0;
        });
    }

    public String getProductNameById(UUID productId) {
        String sql = "SELECT name FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, productId);
    }

    public String getProductDescriptionById(UUID productId) {
        // Предположим, что в таблице есть поле description
        // Если такого поля нет — можно возвращать, например, name или null
        String sql = "SELECT description FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, productId);
    }
    // Кеш для hasProductOfTypeByUser
    private final Cache<CacheKey, Boolean> productTypeCache = CacheProperties.Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    // Кеш для getSumOfTransactionsByUserAndProductAndTransactionType
    private final Cache<CacheKey2, Long> transactionSumCache = CacheProperties.Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    // Кеш для isActiveUserOfProductType
    private final Cache<CacheKey, Boolean> activeUserCache = CacheProperties.Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    // Вспомогательные классы для ключей
    private record CacheKey(UUID userId, String productType) {}

    private record CacheKey2(UUID userId, String productType, String transactionType) {}
}