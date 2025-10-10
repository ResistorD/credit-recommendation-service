CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    income DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    user_id UUID,
    amount DECIMAL(10,2),
    category VARCHAR(50),
    timestamp TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    type VARCHAR(50),
    description VARCHAR(255),
    interest_rate DECIMAL(5,2),
    fee DECIMAL(10,2)
);
