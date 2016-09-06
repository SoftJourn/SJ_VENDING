ALTER TABLE products ADD imageData MEDIUMBLOB;

CREATE TABLE favorites
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    account VARCHAR(255),
    product INT(11),
    CONSTRAINT FK_6knm90flkwduiq318ftuc1dgd FOREIGN KEY (product) REFERENCES products (id)
);
CREATE INDEX FK_6knm90flkwduiq318ftuc1dgd ON favorites (product);
CREATE UNIQUE INDEX UK_idou9b2ifkx5035yonqkx48ey ON favorites (account, product);