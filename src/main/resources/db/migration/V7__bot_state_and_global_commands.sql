-- Create table to persist bot runtime state
CREATE TABLE IF NOT EXISTS bot_runtime_state (
    bot_id BIGINT PRIMARY KEY REFERENCES telegram_bots(id) ON DELETE CASCADE,
    is_running BOOLEAN NOT NULL DEFAULT FALSE,
    last_started_at TIMESTAMP,
    last_stopped_at TIMESTAMP,
    last_error TEXT,
    loaded_plugins JSONB,
    session_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Trigger to update updated_at
CREATE OR REPLACE FUNCTION update_bot_runtime_state_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS trg_update_bot_runtime_state_updated_at ON bot_runtime_state;
CREATE TRIGGER trg_update_bot_runtime_state_updated_at 
    BEFORE UPDATE ON bot_runtime_state 
    FOR EACH ROW 
    EXECUTE FUNCTION update_bot_runtime_state_updated_at();

-- Allow global (default) commands by making bot_id nullable
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'bot_commands' AND column_name = 'bot_id' AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE bot_commands ALTER COLUMN bot_id DROP NOT NULL;
    END IF;
END $$;
