-- ============================================================
-- SAFE UPGRADE FOR AN EXISTING WORKING DATABASE
-- Run this only if your current database is missing columns.
-- It does NOT delete existing banking data.
-- ============================================================

ALTER TABLE app_users ADD COLUMN IF NOT EXISTS enabled BOOLEAN DEFAULT TRUE;
UPDATE app_users SET enabled = TRUE WHERE enabled IS NULL;
ALTER TABLE app_users ALTER COLUMN enabled SET NOT NULL;

ALTER TABLE accounts ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;
UPDATE accounts SET active = TRUE WHERE active IS NULL;
ALTER TABLE accounts ALTER COLUMN active SET NOT NULL;

ALTER TABLE transactions ADD COLUMN IF NOT EXISTS type VARCHAR(255) DEFAULT 'DEPOSIT';
UPDATE transactions SET type = 'DEPOSIT' WHERE type IS NULL;
ALTER TABLE transactions ALTER COLUMN type SET NOT NULL;

ALTER TABLE transactions ADD COLUMN IF NOT EXISTS performed_by VARCHAR(255);

ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS details VARCHAR(255);
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
