CREATE TABLE web_user
(
    web_user_pk INT          NOT NULL AUTO_INCREMENT,
    username    varchar(500) NOT NULL,
    password    varchar(500) NOT NULL,
    enabled     BOOLEAN      NOT NULL,
    authorities JSON         NOT NULL,

    PRIMARY KEY (web_user_pk),
    UNIQUE KEY (username),

    CONSTRAINT authorities_must_be_array CHECK (json_type(authorities) = convert('ARRAY' using utf8))
);
