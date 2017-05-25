SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sj_vending`
--

DELIMITER $$
--
-- Procedures
--
DROP PROCEDURE IF EXISTS `dump_image`$$
CREATE PROCEDURE `dump_image` ()  begin

  declare this_id int;
  declare cur1 cursor for select id from images;
  open cur1;
  read_loop: loop
    fetch cur1 into this_id;
    set @query = concat('select data from images where id=',
                        this_id, ' into dumpfile "/var/lib/mysql-files/', this_id);
    prepare write_file from @query;
    execute write_file;
  end loop;
  close cur1;
end$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
CREATE TABLE IF NOT EXISTS `favorites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) DEFAULT NULL,
  `product` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_idou9b2ifkx5035yonqkx48ey` (`account`,`product`),
  KEY `FK_6knm90flkwduiq318ftuc1dgd` (`product`)
) ENGINE=InnoDB AUTO_INCREMENT=7161 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `fields`
--

DROP TABLE IF EXISTS `fields`;
CREATE TABLE IF NOT EXISTS `fields` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL,
  `internal_id` varchar(255) NOT NULL,
  `position` int(11) DEFAULT NULL,
  `product` int(11) DEFAULT NULL,
  `loaded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_3dlqg7ao6x5xew3nnmer8equo` (`product`)
) ENGINE=InnoDB AUTO_INCREMENT=435 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
CREATE TABLE IF NOT EXISTS `images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` mediumblob,
  `product_id` int(11) NOT NULL,
  `resolution` varchar(255) DEFAULT NULL,
  `is_cover` tinyint(1) DEFAULT '0',
  `url` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=400 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `load_history`
--

DROP TABLE IF EXISTS `load_history`;
CREATE TABLE IF NOT EXISTS `load_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `price` decimal(19,2) NOT NULL,
  `date_added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_distributed` tinyint(1) NOT NULL DEFAULT '0',
  `machine_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `load_history_machines_id_fk` (`machine_id`)
) ENGINE=InnoDB AUTO_INCREMENT=418 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `machines`
--

DROP TABLE IF EXISTS `machines`;
CREATE TABLE IF NOT EXISTS `machines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `unique_id` varchar(36) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `cell_limit` int(11) NOT NULL DEFAULT '6',
  PRIMARY KEY (`id`),
  UNIQUE KEY `machines_name_uindex` (`name`),
  KEY `machines_unique_id_uindex` (`unique_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `machine_rows`
--

DROP TABLE IF EXISTS `machine_rows`;
CREATE TABLE IF NOT EXISTS `machine_rows` (
  `machine` int(11) NOT NULL,
  `row` int(11) NOT NULL,
  UNIQUE KEY `UK_5voaw43luxqyqoofooogt7nlw` (`row`),
  KEY `FK_ea7948qsiyjwcj5grhxpesiq2` (`machine`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `added_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text,
  `id_categories` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `FK_8l3my8ls6adnevblpj5twva2e` (`id_categories`),
  KEY `FK_product_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `product_nutrition_facts`
--

DROP TABLE IF EXISTS `product_nutrition_facts`;
CREATE TABLE IF NOT EXISTS `product_nutrition_facts` (
  `product_id` int(11) NOT NULL,
  `nutrition_facts_key` varchar(255) NOT NULL,
  `nutrition_facts` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`,`nutrition_facts_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;
CREATE TABLE IF NOT EXISTS `purchases` (
  `account` varchar(255) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `machine` int(11) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) NOT NULL,
  `product_price` decimal(19,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_machine_i` (`machine`)
) ENGINE=InnoDB AUTO_INCREMENT=936 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `rows`
--

DROP TABLE IF EXISTS `rows`;
CREATE TABLE IF NOT EXISTS `rows` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `row_fields`
--

DROP TABLE IF EXISTS `row_fields`;
CREATE TABLE IF NOT EXISTS `row_fields` (
  `row` int(11) NOT NULL,
  `field` int(11) NOT NULL,
  UNIQUE KEY `UK_sovjpubfae0t9yfuotuengu81` (`field`),
  KEY `FK_m07odu6ul2it8huaw8omfs5cg` (`row`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Constraints for dumped tables
--

--
-- Constraints for table `favorites`
--
ALTER TABLE `favorites`
  ADD CONSTRAINT `FK_6knm90flkwduiq318ftuc1dgd` FOREIGN KEY (`product`) REFERENCES `products` (`id`);

--
-- Constraints for table `fields`
--
ALTER TABLE `fields`
  ADD CONSTRAINT `FK_3dlqg7ao6x5xew3nnmer8equo` FOREIGN KEY (`product`) REFERENCES `products` (`id`);

--
-- Constraints for table `load_history`
--
ALTER TABLE `load_history`
  ADD CONSTRAINT `load_history_machines_id_fk` FOREIGN KEY (`machine_id`) REFERENCES `machines` (`id`);

--
-- Constraints for table `machine_rows`
--
ALTER TABLE `machine_rows`
  ADD CONSTRAINT `FK_5voaw43luxqyqoofooogt7nlw` FOREIGN KEY (`row`) REFERENCES `rows` (`id`),
  ADD CONSTRAINT `FK_ea7948qsiyjwcj5grhxpesiq2` FOREIGN KEY (`machine`) REFERENCES `machines` (`id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `FK_8l3my8ls6adnevblpj5twva2e` FOREIGN KEY (`id_categories`) REFERENCES `categories` (`id`);

--
-- Constraints for table `product_nutrition_facts`
--
ALTER TABLE `product_nutrition_facts`
  ADD CONSTRAINT `FK_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `purchases`
--
ALTER TABLE `purchases`
  ADD CONSTRAINT `FK_machine` FOREIGN KEY (`machine`) REFERENCES `machines` (`id`);

--
-- Constraints for table `row_fields`
--
ALTER TABLE `row_fields`
  ADD CONSTRAINT `FK_m07odu6ul2it8huaw8omfs5cg` FOREIGN KEY (`row`) REFERENCES `rows` (`id`),
  ADD CONSTRAINT `FK_sovjpubfae0t9yfuotuengu81` FOREIGN KEY (`field`) REFERENCES `fields` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
