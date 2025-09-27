ALTER TABLE gateway_partner
ADD COLUMN token_hash VARCHAR(60) AFTER token;

UPDATE gateway_partner
SET token_hash = NULL
WHERE token IS NOT NULL;