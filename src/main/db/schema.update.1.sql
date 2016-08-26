CREATE TABLE purchases
(
    id DOUBLE PRIMARY KEY NOT NULL AUTO_INCREMENT,
    account VARCHAR(255),
    time TINYBLOB,
    product INT(11),
    CONSTRAINT FK_product FOREIGN KEY (product) REFERENCES products (id)
);
CREATE INDEX FK_product ON purchases (product);