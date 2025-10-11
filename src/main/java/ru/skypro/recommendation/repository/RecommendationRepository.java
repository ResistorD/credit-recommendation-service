package ru.skypro.recommendation.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean hasProductOfTypeByUser(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
            WHERE t.USER_ID = ? AND p.TYPE = ?
            """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
    }

    public boolean isActiveUserOfProductType(UUID userId, String productType) {
        String sql = """
        SELECT COUNT(*) >= 5
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
        WHERE t.USER_ID = ? AND p.TYPE = ?
        """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
    }

    public long getSumOfTransactionsByUserAndProductType(UUID userId, String productType, String transactionType) {
        String sql = """
            SELECT SUM(t.AMOUNT)
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
            WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?
            """;
        Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);
        return sum != null ? sum : 0;
    }
    public long getSumOfTransactionsByUserAndProductAndTransactionType(UUID userId, String productType, String transactionType) {
        String sql = """
        SELECT SUM(t.AMOUNT)
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
        WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?
        """;
        Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);
        return sum != null ? sum : 0;
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
}