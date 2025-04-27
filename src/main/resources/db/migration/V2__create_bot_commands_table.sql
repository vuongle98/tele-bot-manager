CREATE TABLE bot_commands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bot_id BIGINT NOT NULL,
    command VARCHAR(255) NOT NULL,
    response_template VARCHAR(1000) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    additional_config TEXT,
    FOREIGN KEY (bot_id) REFERENCES telegram_bots(id)
);

-- Create index on bot_id and command for faster lookups
CREATE INDEX idx_bot_commands_bot_id ON bot_commands(bot_id);
CREATE INDEX idx_bot_commands_command ON bot_commands(command);
CREATE UNIQUE INDEX idx_bot_commands_bot_command ON bot_commands(bot_id, command);
