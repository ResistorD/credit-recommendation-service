CREATE TABLE IF NOT EXISTS rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(255),
    rule_description VARCHAR(1000)
    rule_json TEXT,
    recommended_product_id (UUID)
);
