START TRANSACTION;

-- 1. create user_ocpp_tag table where a user can have multiple ocpp_tags
CREATE TABLE user_ocpp_tag
(
    user_pk int(11) NOT NULL,
    ocpp_tag_pk int(11) NOT NULL,

    PRIMARY KEY (user_pk, ocpp_tag_pk),

    -- ensure that each ocpp_tag can only be assigned to one user
    --
    -- IMPORTANT! previous ocpp_tag_pk column in user table was not unique (more than one user could have
    -- the same ocpp_tag), so this is a change.
    --
    UNIQUE (ocpp_tag_pk),

    FOREIGN KEY (user_pk) REFERENCES user(user_pk) ON DELETE CASCADE,
    FOREIGN KEY (ocpp_tag_pk) REFERENCES ocpp_tag(ocpp_tag_pk) ON DELETE CASCADE
);

-- 2. move data from user table to user_ocpp_tag table
--
-- IMPORTANT! this will fail if there are multiple users with the same ocpp_tag_pk (see above).
-- if you have such data, you need to resolve it before running this migration. we are not making any assumptions or
-- decisions about which user should keep the ocpp_tag (which might be a very problematic assumption), so we just fail
-- the migration.
--
INSERT INTO user_ocpp_tag (user_pk, ocpp_tag_pk)
SELECT user_pk, ocpp_tag_pk FROM user WHERE ocpp_tag_pk IS NOT NULL;

-- 3. now that we moved the data, drop redundant columns
ALTER TABLE user
    DROP FOREIGN KEY FK_user_ocpp_tag_otpk,
    DROP COLUMN ocpp_tag_pk;

COMMIT;
