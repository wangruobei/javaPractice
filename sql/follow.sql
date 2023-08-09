DROP TABLE IF EXISTS `Follow`;
SET character_set_client = utf8mb4 ;
CREATE TABLE `Follow` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `userId` bigint NOT NULL,
    `followerId` bigint NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`userId`) REFERENCES User(`id`),
    FOREIGN KEY (`followerId`) REFERENCES User(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

