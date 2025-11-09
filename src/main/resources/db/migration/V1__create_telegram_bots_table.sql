-- Create telegram_bots table
CREATE TABLE telegram_bots (
    id BIGSERIAL PRIMARY KEY,
    bot_token VARCHAR(255) NOT NULL UNIQUE,
    bot_username VARCHAR(255) NOT NULL UNIQUE,
    bot_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'INACTIVE',
    webhook_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- Create indexes for better performance
CREATE INDEX idx_telegram_bots_bot_token ON telegram_bots(bot_token);
CREATE INDEX idx_telegram_bots_bot_username ON telegram_bots(bot_username);
CREATE INDEX idx_telegram_bots_status ON telegram_bots(status);
CREATE INDEX idx_telegram_bots_is_active ON telegram_bots(is_active);
CREATE INDEX idx_telegram_bots_created_by ON telegram_bots(created_by);
CREATE INDEX idx_telegram_bots_created_at ON telegram_bots(created_at);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_telegram_bots_updated_at 
    BEFORE UPDATE ON telegram_bots 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
