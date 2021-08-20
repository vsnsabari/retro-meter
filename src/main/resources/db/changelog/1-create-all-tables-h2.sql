CREATE TABLE `SESSION` (
  `session_id` CHAR(36) NOT NULL PRIMARY KEY,
  `started_by` varchar(255) NOT NULL,
  `team` varchar(255) NOT NULL,
  `created_date` DATETIME NOT NULL
);

CREATE TABLE `COMMENT` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` CHAR(36) NOT NULL,
  `comment_text` varchar(MAX) NOT NULL,
  `comment_type` char(1) NOT NULL,
  `up_votes` int DEFAULT 0,
  `down_votes` int DEFAULT 0,
  `created_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_session_id` FOREIGN KEY (`session_id`) REFERENCES `SESSION` (`session_id`)
);
