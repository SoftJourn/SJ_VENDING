
ALTER TABLE categories MODIFY COLUMN `name` VARCHAR(255) Not NULL UNIQUE;
ALTER TABLE products MODIFY COLUMN `name` VARCHAR(255) Not NULL UNIQUE;