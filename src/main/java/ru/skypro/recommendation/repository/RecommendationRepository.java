package ru.skypro.recommendation.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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

    /** Есть ли у пользователя продукт указанного типа */
    public boolean hasProductOfTypeByUser(UUID userId, String productType) {
        CacheKey key = new CacheKey(userId, productType);
        return productTypeCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) > 0
                    FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ?
                    """;
            Boolean has = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
            return has != null && has;
        });
    }

    /** Активный ли пользователь по типу продукта (пример: >= 5 транзакций) */
    public boolean isActiveUserOfProductType(UUID userId, String productType) {
        CacheKey key = new CacheKey(userId, productType);
        return activeUserCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) >= 5
                    FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ?
                    """;
            Boolean active = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
            return active != null && active;
        });
    }

    /** Делегируем в метод с корректным кешом Long */
    public long getSumOfTransactionsByUserAndProductType(UUID userId, String productType, String transactionType) {
        return getSumOfTransactionsByUserAndProductAndTransactionType(userId, productType, transactionType);
    }

    /** Сумма по user+productType+transactionType (кеш Long) */
    public long getSumOfTransactionsByUserAndProductAndTransactionType(UUID userId, String productType, String transactionType) {
        CacheKey2 key = new CacheKey2(userId, productType, transactionType);
        return transactionSumCache.get(key, k -> {
            String sql = """
                    SELECT COALESCE(SUM(t.amount), 0)
                    FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ? AND t.type = ?
                    """;
            Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);
            return sum != null ? sum : 0L;
        });
    }

    public String getProductNameById(UUID productId) {
        String sql = "SELECT name FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, productId);
    }

    public String getProductDescriptionById(UUID productId) {
        String sql = "SELECT description FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, productId);
    }

    // Кеши
    private final Cache<CacheKey, Boolean> productTypeCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final Cache<CacheKey2, Long> transactionSumCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final Cache<CacheKey, Boolean> activeUserCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    // Ключи кешей
    private record CacheKey(UUID userId, String productType) {}
    private record CacheKey2(UUID userId, String productType, String transactionType) {}
}
