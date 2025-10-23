CREATE TABLE rule_stats (
    rule_id UUID NOT NULL,
    hit_count BIGINT DEFAULT 0,
    PRIMARY KEY (rule_id),
    FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE
);