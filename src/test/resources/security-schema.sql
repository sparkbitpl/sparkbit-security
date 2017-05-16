DROP TABLE IF EXISTS session;
DROP TABLE if EXISTS user_role;
DROP TABLE IF EXISTS uzer;

CREATE TABLE uzer (
  id               VARCHAR(32) NOT NULL,
  username         VARCHAR(50) NOT NULL,
  password         VARCHAR(82) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_role (
  user_id VARCHAR(32) NOT NULL,
  role    VARCHAR(50) NOT NULL,
  PRIMARY KEY (user_id, role),
  FOREIGN KEY (user_id) REFERENCES uzer (id)
    ON DELETE CASCADE
);

CREATE TABLE session (
  id          VARCHAR(32) NOT NULL,
  user_id     VARCHAR(32) NOT NULL,
  creation_ts BIGINT      NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES uzer (id)
);
