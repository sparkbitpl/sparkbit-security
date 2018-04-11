DROP TABLE IF EXISTS user_security_challenge;
DROP TABLE IF EXISTS user_session;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user_credentials;

CREATE TABLE user_credentials (
  user_id  VARCHAR(32) NOT NULL,
  password VARCHAR(82) NOT NULL,
  enabled  BOOLEAN     NOT NULL,
  deleted  BOOLEAN     NOT NULL,
  PRIMARY KEY (user_id)
);

CREATE TABLE user_role (
  user_id VARCHAR(32) NOT NULL,
  role    VARCHAR(50) NOT NULL,
  PRIMARY KEY (user_id, role),
  FOREIGN KEY (user_id) REFERENCES user_credentials (user_id)
);

CREATE TABLE user_session (
  auth_token_hash            VARCHAR(64) NOT NULL,
  user_id                    VARCHAR(32) NOT NULL,
  creation_ts                BIGINT      NOT NULL,
  expiration_ts              BIGINT,
  deletion_ts                BIGINT,
  extra_authn_check_required BOOLEAN     NOT NULL DEFAULT FALSE,
  PRIMARY KEY (auth_token_hash),
  FOREIGN KEY (user_id) REFERENCES user_credentials (user_id)
);

CREATE TABLE user_security_challenge (
  id             VARCHAR(32) NOT NULL,
  user_id        VARCHAR(32) NOT NULL,
  challenge_type VARCHAR(32) NOT NULL,
  expiration_ts  BIGINT      NOT NULL,
  token          VARCHAR(32) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (token, challenge_type),
  FOREIGN KEY (user_id) REFERENCES user_credentials (user_id)
);
