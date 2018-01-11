DROP TABLE IF EXISTS simple_login_user;
DROP TABLE IF EXISTS composite_login_user;

CREATE TABLE simple_login_user (
  id       VARCHAR(32) NOT NULL,
  username VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE composite_login_user (
  id       VARCHAR(32) NOT NULL,
  username VARCHAR(64) NOT NULL,
  context  VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
);
