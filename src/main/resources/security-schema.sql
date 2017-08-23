DROP TABLE IF EXISTS user_session;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user_credentials;

CREATE TABLE user_credentials (
  user_id  VARCHAR(32) NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(82) NOT NULL,
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
  PRIMARY KEY (auth_token),
  FOREIGN KEY (user_id) REFERENCES user_credentials (user_id)
);
