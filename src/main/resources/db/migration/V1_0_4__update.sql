-- adding tables for multi users access

CREATE TABLE `webusers` (
  `username` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb3_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE `webauthorities` (
  `username` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL,
  `authority` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'ROLE_USER',
  UNIQUE KEY `authorities_idx_1` (`username`,`authority`),
  CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`username`) REFERENCES `webusers` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE UNIQUE INDEX ix_auth_username
  on webauthorities (username,authority);


-- Insert a user = admin with the password = pass. Change password after installing!
INSERT INTO webusers (username, password, enabled)
  values ('admin',
    '$2a$10$.Rxx4JnuX8OGJTIOCXn76euuB3dIGHHrkX9tswYt9ECKjAGyms30W',
    1);

INSERT INTO webauthorities (username, authority)
  values ('admin', 'ROLE_ADMIN');
	
	