-- Add bot_id column to plugins table to support bot-specific plugins
ALTER TABLE plugins ADD COLUMN IF NOT EXISTS bot_id BIGINT;

-- Add foreign key constraint
ALTER TABLE plugins 
    ADD CONSTRAINT fk_plugin_bot 
    FOREIGN KEY (bot_id) 
    REFERENCES telegram_bots(id) 
    ON DELETE SET NULL;

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_plugins_bot_id ON plugins(bot_id);
