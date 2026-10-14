-- Align api_keys schema and constraints for A2A materials endpoint
ALTER TABLE api_keys ALTER COLUMN key_identity DROP NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_api_keys_key_hash ON api_keys(key_hash);
