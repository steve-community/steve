-- Password Migration Guide for OCPP 2.0.1 Authentication
-- This migration adds documentation for password setup

-- The V1_2_1__add_auth_password.sql migration added the auth_password column.
-- This file documents the password setup process for existing charge points.

-- For each charge point that needs authentication, generate a BCrypt hash:
-- 1. Use the PasswordHashUtil utility:
--    java -cp target/steve.war de.rwth.idsg.steve.utils.PasswordHashUtil <your-password>
--
-- 2. Or use bcrypt command line:
--    htpasswd -bnBC 10 "" <your-password> | tr -d ':\n'
--
-- 3. Set the password for a charge point:
--    UPDATE charge_box
--    SET auth_password = '$2a$10$...'
--    WHERE charge_box_id = 'CP001';

-- Example: Set password for all charge points (NOT RECOMMENDED for production)
-- UPDATE charge_box
-- SET auth_password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
-- WHERE charge_box_id IS NOT NULL;
-- (Above hash is for password: 'changeme')

-- To disable authentication for a specific charge point:
-- UPDATE charge_box SET auth_password = NULL WHERE charge_box_id = 'CP001';

-- Check which charge points have passwords configured:
-- SELECT charge_box_id,
--        CASE
--            WHEN auth_password IS NULL THEN 'No auth (backward compatible)'
--            ELSE 'Auth enabled'
--        END as auth_status
-- FROM charge_box;
