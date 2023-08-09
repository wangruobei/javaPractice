DROP TABLE IF EXISTS `User`;
SET character_set_client = utf8mb4 ;
CREATE TABLE `User` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL unique,
    `password` char(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
