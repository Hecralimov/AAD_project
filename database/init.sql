-- AAD E-Commerce: Pure Structural Schema (No Data)
-- Ready for Python Pandas Appending

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Categories
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Users (Updated with is_active)
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `role_type` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Customer Profiles (Task A18 Addition)
DROP TABLE IF EXISTS `customer_profiles`;
CREATE TABLE `customer_profiles` (
  `id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `address_line` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Stores
DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores` (
  `id` varchar(255) NOT NULL,
  `owner_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Products
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `id` varchar(255) NOT NULL,
  `store_id` varchar(255) DEFAULT NULL,
  `category_id` varchar(255) DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `unit_price` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Orders
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `store_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `grand_total` decimal(38,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Order Items
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
  `id` varchar(255) NOT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Reviews
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `star_rating` int DEFAULT NULL,
  `sentiment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Shipments
DROP TABLE IF EXISTS `shipments`;
CREATE TABLE `shipments` (
  `id` varchar(255) NOT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `warehouse` varchar(255) DEFAULT NULL,
  `mode` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
