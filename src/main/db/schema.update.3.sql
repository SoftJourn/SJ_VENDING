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