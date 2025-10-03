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

    private List<Recommendation> getAllRecommendations() {
        // Здесь можно загрузить рекомендации из конфигурации (например, YAML, JSON или Map)
        return loadRecommendationsFromConfig();
    }

    private List<Recommendation> loadRecommendationsFromConfig() {
        // Пример загрузки из памяти
        List<Rule> invest500Rules = List.of(
                new HasDebitProductRule(),
                new DoesNotHaveInvestProductRule(),
                new TotalSavingDepositOverRule(1000)
        );
        List<Rule> topSavingRules = List.of(
                new HasDebitProductRule(), // 1
                new DepositAmountThresholdRule(50000), // 2
                new DepositGreaterThanSpendingRule() // 3
        );

        List<Rule> simpleCreditRules = List.of(
                new DoesNotHaveCreditProductRule(), // 1
                new DepositGreaterThanSpendingRule(), // 2
                new TotalSpendingOverThresholdRule(100000) // 3
        );

        Recommendation invest500 = new Recommendation(
                "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                "Invest 500",
                "INVEST",
                "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!",
                invest500Rules
        );
        Recommendation topSaving = new Recommendation(
                "59efc529-2fff-41af-baff-90ccd7402925",
                "Top Saving",
                "SAVING",
                "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем! Преимущества «Копилки»: Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет. Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг. Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!",
                topSavingRules
        );
        Recommendation simpleCredit = new Recommendation(
                "ab138afb-f3ba-4a93-b74f-0fcee86d447f",
                "Простой кредит",
                "CREDIT",
                "Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту. Почему выбирают нас:  Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов. Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении. Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое. Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!",
                simpleCreditRules
        );

        return List.of(invest500, topSaving, simpleCredit);
    }

    public List<Recommendation> getMatchingRecommendationsForUser(String userId) {
        User user = getUserById(userId);
        List<Transaction> userTransactions = getTransactionsByUserId(userId);
        List<Product> userProducts = getProductsByUserId(userId);

        List<Recommendation> allRecommendations = getAllRecommendations();
        List<Recommendation> matchingRecommendations = new ArrayList<>();

        for (Recommendation recommendation : allRecommendations) {
            boolean allRulesMatch = recommendation.getRules().stream()
                    .allMatch(rule -> rule.matches(user, userTransactions, userProducts));

            if (allRulesMatch) {
                matchingRecommendations.add(recommendation);
            }
        }

        return matchingRecommendations;
    }
}
