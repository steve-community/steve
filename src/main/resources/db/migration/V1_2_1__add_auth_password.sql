ALTER TABLE charge_box
ADD COLUMN auth_password VARCHAR(255) DEFAULT NULL
COMMENT 'BCrypt hashed password for Basic Authentication';

CREATE INDEX idx_charge_box_auth ON charge_box(charge_box_id, auth_password);