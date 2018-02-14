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
  auth_token  VARCHAR(32) NOT NULL,
  user_id     VARCHAR(32) NOT NULL,
  creation_ts BIGINT      NOT NULL,
  deleted_ts  BIGINT,
  expires_at  BIGINT,
  PRIMARY KEY (auth_token),
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
