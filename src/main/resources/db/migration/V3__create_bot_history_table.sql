CREATE TABLE bot_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bot_id BIGINT NOT NULL,
    previous_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    notes VARCHAR(1000),
    error_details VARCHAR(2000),
    FOREIGN KEY (bot_id) REFERENCES telegram_bots(id)
);

-- Create indexes for faster querying
CREATE INDEX idx_bot_history_bot_id ON bot_history(bot_id);
CREATE INDEX idx_bot_history_timestamp ON bot_history(timestamp);
