CREATE TABLE `users` (
	`id` INT UNSIGNED AUTO_INCREMENT,
	`email` VARCHAR (256),
	`username` VARCHAR (32) NOT NULL,
	`is_email_confirmed` TINYINT (1),
	`password` VARCHAR (128),
	`salt` VARCHAR (12),
	`last_login_ip` VARCHAR (64),
	`last_login_time` datetime,
	`created_at` TIMESTAMP NULL,
	`updated_at` TIMESTAMP NULL,
	PRIMARY KEY (`id`)
);
CREATE UNIQUE INDEX uix_users_email ON `users` (`email`);
CREATE UNIQUE INDEX uix_users_username ON `users` (`username`);