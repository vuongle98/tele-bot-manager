-- Create bot_history table for audit trail
CREATE TABLE IF NOT EXISTS bot_history (
    id BIGSERIAL PRIMARY KEY,
    bot_id BIGINT NOT NULL REFERENCES telegram_bots(id) ON DELETE CASCADE,
    previous_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    error_details TEXT,
    triggered_by VARCHAR(255),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_bot_history_bot_id ON bot_history(bot_id);
CREATE INDEX idx_bot_history_timestamp ON bot_history(timestamp DESC);
CREATE INDEX idx_bot_history_new_status ON bot_history(new_status);
