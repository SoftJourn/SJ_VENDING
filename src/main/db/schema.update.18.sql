ALTER TABLE purchases ADD COLUMN product_name VARCHAR(255) NOT NULL;
ALTER TABLE purchases ADD COLUMN product_price DECIMAL(19, 2) NOT NULL;

ALTER TABLE purchases MODIFY COLUMN time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE fields MODIFY COLUMN loaded timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products MODIFY COLUMN added_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE purchases SET product_name = (SELECT products.name FROM products WHERE products.id = purchases.product);
UPDATE purchases SET product_price = (SELECT products.price FROM products WHERE products.id = purchases.product);

ALTER TABLE purchases DROP FOREIGN KEY FK_product;
ALTER TABLE purchases DROP COLUMN product;