CREATE TABLE prices (
  id         INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  prices     NUMERIC(19, 2)  NOT NULL,
  time       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  product_id INT             NOT NULL
);

INSERT INTO prices (prices, time, product_id) SELECT
                                                products.price,
                                                products.added_time,
                                                products.id
                                              FROM products;

ALTER TABLE prices
  ADD CONSTRAINT FK_prices_to_products FOREIGN KEY (product_id) REFERENCES products (id);

DELIMITER //
CREATE TRIGGER UpdatePrice
AFTER UPDATE ON products
FOR EACH ROW
  BEGIN
    IF new.price <> old.price
    THEN
      INSERT INTO prices (prices, product_id) VALUES (new.price, new.id);
    END IF;
  END;
//
DELIMITER ;

DELETE FROM load_history;

ALTER TABLE load_history
  ADD COLUMN product INT NOT NULL;
ALTER TABLE load_history
  ADD COLUMN field INT NOT NULL;
ALTER TABLE load_history
  ADD COLUMN count INT NOT NULL;
ALTER TABLE load_history
  ADD COLUMN total DECIMAL(19, 2) NOT NULL;
ALTER TABLE load_history
  ADD COLUMN hash VARCHAR(250) NOT NULL;

INSERT INTO load_history (total) SELECT load_history.price
                                 FROM load_history;

ALTER TABLE load_history
  ADD CONSTRAINT FK_load_history_to_products FOREIGN KEY (product) REFERENCES products (id);

ALTER TABLE load_history
  ADD CONSTRAINT FK_load_history_to_fields FOREIGN KEY (field) REFERENCES fields (id);
