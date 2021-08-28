CREATE TABLE SESSION (
  session_id CHAR(36) NOT NULL PRIMARY KEY,
  started_by varchar(255) NOT NULL,
  team varchar(255) NOT NULL,
  created_date DATETIME NOT NULL
);

CREATE TABLE COMMENT (
  id bigint NOT NULL IDENTITY PRIMARY KEY,
  session_id CHAR(36) NOT NULL,
  added_by CHAR(255) NOT NULL,
  comment_text varchar(MAX) NOT NULL,
  comment_type CHAR(1) NOT NULL,
  likes int,
  is_action_item bit NOT NULL,
  created_date DATETIME NOT NULL,
  CONSTRAINT fk_session_id FOREIGN KEY (session_id) REFERENCES SESSION (session_id)
);
