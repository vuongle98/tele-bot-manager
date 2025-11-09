-- Create command_executions table for tracking command execution history
CREATE TABLE command_executions (
    id BIGSERIAL PRIMARY KEY,
    command_id BIGINT NOT NULL,
    execution_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    input_data TEXT,
    output_data TEXT,
    error_message VARCHAR(2000),
    execution_time_ms BIGINT,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    triggered_by VARCHAR(255),
    user_id VARCHAR(255),
    chat_id VARCHAR(255),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (command_id) REFERENCES bot_commands(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_command_executions_command_id ON command_executions(command_id);
CREATE INDEX idx_command_executions_execution_id ON command_executions(execution_id);
CREATE INDEX idx_command_executions_status ON command_executions(status);
CREATE INDEX idx_command_executions_started_at ON command_executions(started_at);
CREATE INDEX idx_command_executions_user_id ON command_executions(user_id);
CREATE INDEX idx_command_executions_chat_id ON command_executions(chat_id);
CREATE INDEX idx_command_executions_triggered_by ON command_executions(triggered_by);
