CREATE TABLE categories
(
  id BIGINT PRIMARY KEY NOT NULL IDENTITY,
  name VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX name ON categories (name);
CREATE TABLE machines
(
  id INT PRIMARY KEY NOT NULL IDENTITY,
  name VARCHAR(255),
  url VARCHAR(255) NOT NULL,
  unique_id VARCHAR(36) NOT NULL,
  is_active BOOLEAN NOT NULL,
  cell_limit INT DEFAULT '6' NOT NULL
);
CREATE UNIQUE INDEX machines_name_uindex ON machines (name);
CREATE TABLE products
(
  id INT PRIMARY KEY NOT NULL IDENTITY,
  name VARCHAR(255) NOT NULL,
  price NUMERIC(19,2) NOT NULL,
  added_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  description LONGVARCHAR,
  id_categories BIGINT,
  CONSTRAINT FK_8l3my8ls6adnevblpj5twva2e FOREIGN KEY (id_categories) REFERENCES categories (id)
);
CREATE INDEX machines_unique_id_uindex ON machines (unique_id);
CREATE INDEX FK_8l3my8ls6adnevblpj5twva2e ON products (id_categories);
CREATE TABLE purchases
(
  account VARCHAR(255),
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  product_name VARCHAR(255),
  product_price NUMERIC,
  machine INT,
  id BIGINT PRIMARY KEY NOT NULL IDENTITY,
  CONSTRAINT FK_machine FOREIGN KEY (machine) REFERENCES machines (id)
);
CREATE INDEX FK_machine_i ON purchases (machine);
CREATE TABLE rows
(
  id INT PRIMARY KEY NOT NULL IDENTITY,
  row_id VARCHAR(255)
);
CREATE TABLE favorites
(
  id BIGINT PRIMARY KEY NOT NULL IDENTITY,
  account VARCHAR(255),
  product INT,
  CONSTRAINT FK_6knm90flkwduiq318ftuc1dgd FOREIGN KEY (product) REFERENCES products (id)
);
CREATE INDEX FK_6knm90flkwduiq318ftuc1dgd ON favorites (product);
CREATE UNIQUE INDEX UK_idou9b2ifkx5035yonqkx48ey ON favorites (account, product);
CREATE TABLE fields
(
  id INT PRIMARY KEY NOT NULL IDENTITY,
  "COUNT" INT NOT NULL,
  internal_id VARCHAR(255) NOT NULL,
  position INT,
  loaded TIMESTAMP DEFAULT NULL,
  product INT,
  CONSTRAINT FK_3dlqg7ao6x5xew3nnmer8equo FOREIGN KEY (product) REFERENCES products (id)
);
CREATE INDEX FK_3dlqg7ao6x5xew3nnmer8equo ON fields (product);
CREATE TABLE load_history
(
  id BIGINT PRIMARY KEY NOT NULL IDENTITY,
  price NUMERIC(19,2) NOT NULL,
  date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  is_distributed BOOLEAN DEFAULT FALSE NOT NULL,
  machine_id INT NOT NULL,
  CONSTRAINT load_history_machines_id_fk FOREIGN KEY (machine_id) REFERENCES machines (id)
);
CREATE INDEX load_history_machines_id_fk ON load_history (machine_id);
CREATE TABLE machine_rows
(
  machine INT NOT NULL,
  row INT NOT NULL,
  CONSTRAINT FK_ea7948qsiyjwcj5grhxpesiq2 FOREIGN KEY (machine) REFERENCES machines (id),
  CONSTRAINT FK_5voaw43luxqyqoofooogt7nlw FOREIGN KEY (row) REFERENCES rows (id)
);
CREATE INDEX FK_ea7948qsiyjwcj5grhxpesiq2 ON machine_rows (machine);
CREATE UNIQUE INDEX UK_5voaw43luxqyqoofooogt7nlw ON machine_rows (row);

CREATE TABLE row_fields
(
  row INT NOT NULL,
  field INT NOT NULL,
  FOREIGN KEY (row) REFERENCES rows (id),
  FOREIGN KEY (field) REFERENCES fields (id)
);
CREATE TABLE images
(
  id         BIGINT PRIMARY KEY NOT NULL IDENTITY ,
  data       varbinary(262144),
  product_id INT                NOT NULL,
  is_cover   BOOLEAN      DEFAULT FALSE,
  resolution VARCHAR(255),
  url        VARCHAR(255) DEFAULT '',
  CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
);
CREATE TABLE product_nutrition_facts(
  product_id INT NOT NULL,
  nutrition_facts_key VARCHAR(255) NOT NULL,
  nutrition_facts VARCHAR(255),
  PRIMARY KEY (product_id, nutrition_facts_key),
  CONSTRAINT FK_product_id FOREIGN KEY (product_id) REFERENCES products(id)
);