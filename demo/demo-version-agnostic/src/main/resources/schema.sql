CREATE TABLE `team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `manager_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `last_modified_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `team_name` (`name`)
);

CREATE TABLE `person` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `team_id` int(11) NOT NULL,
  `name` varchar(255),
  `year_of_birth` varchar(255),
  `email_address` varchar(255),
  `activation_date` varchar(255),
  `created_date` datetime NOT NULL,
  `last_modified_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `person_email_address` (`email_address`),
  CONSTRAINT `person_team_fk` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
);

ALTER TABLE `team`
ADD CONSTRAINT `team_person_fk`
FOREIGN KEY (`manager_id`) REFERENCES `person` (`id`);

CREATE TABLE `checkin` (
  `person_id` int(11) NOT NULL,
  `checkin_date` date NOT NULL,
  `message` MEDIUMTEXT,
  `created_date` datetime NOT NULL,
  `last_modified_date` datetime NOT NULL,
  PRIMARY KEY (`person_id`, `checkin_date`),
  CONSTRAINT `checkin_person_fk` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
);
