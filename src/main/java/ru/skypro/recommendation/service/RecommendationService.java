package ru.skypro.recommendation.service;

import ru.skypro.recommendation.Rules.*;
import ru.skypro.recommendation.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.ArrayList;
import java.util.List;
@Service
public class RecommendationService {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Вспомогательные методы
    private User getUserById(String userId) {
        String sql = "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME FROM USERS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), userId);
    }

    private List<Transaction> getTransactionsByUserId(String userId) {
        String sql = "SELECT ID, PRODUCT_ID, USER_ID, TYPE, AMOUNT FROM TRANSACTIONS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, new TransactionRowMapper(), userId);
    }

    private List<Product> getProductsByUserId(String userId) {
        String sql = """
            SELECT DISTINCT p.ID, p.TYPE, p.NAME
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
            WHERE t.USER_ID = ?
            """;
        return jdbcTemplate.query(sql, new ProductRowMapper(), userId);
    }


}
