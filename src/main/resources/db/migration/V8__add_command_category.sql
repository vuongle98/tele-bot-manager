-- Add category column to bot_commands for routing
ALTER TABLE bot_commands ADD COLUMN IF NOT EXISTS category VARCHAR(50);

-- Optional: backfill based on existing type/plugin_name heuristics
UPDATE bot_commands
SET category = CASE
    WHEN type = 'PLUGIN' OR plugin_name IS NOT NULL THEN 'PLUGIN'
    WHEN type IN ('AI_TASK','AI_ANSWER','SUMMARY','GENERATION','ANALYSIS') THEN 'AI'
    ELSE 'DEFAULT'
END
WHERE category IS NULL;

CREATE INDEX IF NOT EXISTS idx_bot_commands_category ON bot_commands(category);
