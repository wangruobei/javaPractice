DROP TABLE IF EXISTS `Post`;
SET character_set_client = utf8mb4 ;
CREATE TABLE `Post` (
    `contentId` bigint NOT NULL AUTO_INCREMENT,
    `userId` bigint NOT NULL,
    `title` varchar(100) NOT NULL,
    `content` text,
    `views` bigint DEFAULT 0,
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`contentId`),
    FOREIGN KEY (`userId`) REFERENCES User(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;