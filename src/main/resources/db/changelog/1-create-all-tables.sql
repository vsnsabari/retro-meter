CREATE TABLE SESSION (
  session_id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
  started_by varchar(255) NOT NULL,
  team varchar(255) NOT NULL,
  created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE COMMENT (
  id bigint NOT NULL IDENTITY PRIMARY KEY,
  session_id UNIQUEIDENTIFIER NOT NULL,
  comment_text varchar(MAX) NOT NULL,
  up_votes int DEFAULT 0,
  down_votes int DEFAULT 0,
  created_date DATETIME NOT NULL DEFAULT GETDATE(),
  CONSTRAINT fk_session_id FOREIGN KEY (session_id) REFERENCES SESSION (session_id),
);
