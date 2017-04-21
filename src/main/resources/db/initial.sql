-- INITIAL
CREATE TABLE fields
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  count INT(11) NOT NULL,
  internal_id VARCHAR(255) NOT NULL,
  position INT(11),
  product_id INT(11),
  row_id INT(11)
);

CREATE TABLE machine_rows
(
  machine INT(11) NOT NULL,
  row INT(11) NOT NULL
);

CREATE TABLE machines
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(255)
);
CREATE TABLE products
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  image_url VARCHAR(255),
  name VARCHAR(255) NOT NULL,
  price DECIMAL(19,2) NOT NULL
);
CREATE TABLE row_fields
(
  row INT(11) NOT NULL,
  field INT(11) NOT NULL
);

CREATE TABLE rows
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  row_id VARCHAR(255)
);

ALTER TABLE fields ADD CONSTRAINT FK_52b2srt2w8hmghi1pv8tao6ll FOREIGN KEY (row_id) REFERENCES rows (id);
ALTER TABLE fields ADD CONSTRAINT FK_p22th1vlbrm7wcpr2b9pm4wfn FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE machine_rows ADD CONSTRAINT FK_5voaw43luxqyqoofooogt7nlw FOREIGN KEY (row) REFERENCES rows (id);
ALTER TABLE machine_rows ADD CONSTRAINT FK_ea7948qsiyjwcj5grhxpesiq2 FOREIGN KEY (machine) REFERENCES machines (id);

ALTER TABLE row_fields ADD CONSTRAINT FK_m07odu6ul2it8huaw8omfs5cg FOREIGN KEY (row) REFERENCES rows (id);
ALTER TABLE row_fields ADD CONSTRAINT FK_sovjpubfae0t9yfuotuengu81 FOREIGN KEY (field) REFERENCES fields (id);

CREATE INDEX FK_52b2srt2w8hmghi1pv8tao6ll ON fields (row_id);
CREATE INDEX FK_p22th1vlbrm7wcpr2b9pm4wfn ON fields (product_id);

CREATE INDEX FK_m07odu6ul2it8huaw8omfs5cg ON row_fields (row);
CREATE UNIQUE INDEX UK_sovjpubfae0t9yfuotuengu81 ON row_fields (field);

CREATE INDEX FK_ea7948qsiyjwcj5grhxpesiq2 ON machine_rows (machine);
CREATE UNIQUE INDEX UK_5voaw43luxqyqoofooogt7nlw ON machine_rows (row);

-- UPDATE #1
CREATE TABLE purchases
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  account VARCHAR(255),
  time TINYBLOB,
  product INT(11),
  machine INT(11),
  CONSTRAINT FK_product FOREIGN KEY (product) REFERENCES products (id),
  CONSTRAINT FK_machine FOREIGN KEY (machine) REFERENCES machines (id)
);
CREATE INDEX FK_product ON purchases (product);
CREATE INDEX FK_machine ON purchases (machine);

ALTER TABLE products ADD addedTime TINYBLOB;
ALTER TABLE products ADD category VARCHAR(64);
ALTER TABLE products ADD description TEXT;

-- UPDATE #2
ALTER TABLE products ADD imageData MEDIUMBLOB;
ALTER TABLE machines ADD address VARCHAR(255);

CREATE TABLE favorites
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  account VARCHAR(255),
  product INT(11),
  CONSTRAINT FK_6knm90flkwduiq318ftuc1dgd FOREIGN KEY (product) REFERENCES products (id)
);
CREATE INDEX FK_6knm90flkwduiq318ftuc1dgd ON favorites (product);
CREATE UNIQUE INDEX UK_idou9b2ifkx5035yonqkx48ey ON favorites (account, product);

-- UPDATE #3
CREATE TABLE categories
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
);

ALTER TABLE products DROP COLUMN category;
ALTER TABLE products ADD COLUMN id_categories BIGINT(20);
ALTER TABLE products ADD CONSTRAINT FK_8l3my8ls6adnevblpj5twva2e FOREIGN KEY (id_categories) REFERENCES categories (id);

INSERT INTO categories(id, name) VALUES
  (1,'Drink'),
  (2, 'Snack');

-- UPDATE #4
ALTER TABLE categories MODIFY COLUMN `name` VARCHAR(255) Not NULL UNIQUE;
ALTER TABLE products MODIFY COLUMN `name` VARCHAR(255) Not NULL UNIQUE;

-- UPDATE #5
UPDATE products SET image_url = SUBSTR(image_url, 4);

-- UPDATE #6
ALTER TABLE purchases MODIFY COLUMN time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE products MODIFY COLUMN addedTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE purchases DROP COLUMN id;
ALTER TABLE purchases ADD COLUMN id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT FIRST;

ALTER TABLE fields CHANGE product_id product INT(11);

-- UPDATE #7
ALTER TABLE machines DROP address;
CREATE UNIQUE INDEX machines_name_uindex ON machines (name);

-- UPDATE #8
ALTER TABLE machines ADD url VARCHAR(255) NOT NULL;

-- UPDATE #9
CREATE TABLE load_history
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  price DECIMAL(19,2) NOT NULL,
  date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  is_distributed TINYINT(1) DEFAULT 0 NOT NULL,
  machine_id INT(11) NOT NULL,
  CONSTRAINT load_history_machines_id_fk FOREIGN KEY (machine_id) REFERENCES machines (id)
);

-- UPDATE #10
ALTER TABLE machines ADD unique_id VARCHAR(36) NOT NULL;
CREATE UNIQUE INDEX machines_unique_id_uindex ON sj_vending.machines (unique_id);

ALTER TABLE products CHANGE addedTime added_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products CHANGE imageData image_data MEDIUMBLOB;

-- UPDATE #11
ALTER TABLE machines ADD is_active BIT(1) NOT NULL;

-- UPDATE #12
ALTER TABLE machines
  ADD cell_limit INT NOT NULL DEFAULT 6;

-- UPDATE #13
CREATE TABLE image
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  data MEDIUMBLOB,
  product_id INT(11) NOT NULL,
  resolution VARCHAR(255)
);

-- UPDATE #14
ALTER TABLE products ADD image_urls VARCHAR(255) NULL;

-- UPDATE #15
ALTER TABLE image ADD is_cover BOOLEAN DEFAULT FALSE ;
ALTER TABLE image ADD url VARCHAR(255) DEFAULT '' ;
INSERT INTO image (image.data,image.product_id,image.resolution,image.is_cover)
  SELECT image_data,id,'jpeg',TRUE from products;
UPDATE image
SET url = CONCAT ('products/', image.product_id,'/images/',image.id,'.',image.resolution);

ALTER TABLE products DROP COLUMN image_url;
ALTER TABLE products DROP COLUMN image_urls;
ALTER TABLE products DROP COLUMN image_data;

ALTER TABLE image RENAME images;

-- UPDATE #16
CREATE TABLE product_nutrition_facts(
  product_id INT NOT NULL,
  nutrition_facts_key VARCHAR(255) NOT NULL,
  nutrition_facts VARCHAR(255),
  CONSTRAINT `PRIMARY` PRIMARY KEY (product_id, nutrition_facts_key),
  CONSTRAINT FK_product_id FOREIGN KEY (product_id) REFERENCES products(id)
);
CREATE INDEX FK_product_id ON products(id);

-- UPDATE #17
ALTER TABLE fields ADD COLUMN loaded TIMESTAMP;

-- UPDATE #18
ALTER TABLE purchases ADD COLUMN product_name VARCHAR(255) NOT NULL;
ALTER TABLE purchases ADD COLUMN product_price DECIMAL(19, 2) NOT NULL;

ALTER TABLE purchases MODIFY COLUMN time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE fields MODIFY COLUMN loaded timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products MODIFY COLUMN added_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE purchases SET product_name = (SELECT products.name FROM products WHERE products.id = purchases.product);
UPDATE purchases SET product_price = (SELECT products.price FROM products WHERE products.id = purchases.product);

ALTER TABLE purchases DROP FOREIGN KEY FK_product;
ALTER TABLE purchases DROP COLUMN product;


