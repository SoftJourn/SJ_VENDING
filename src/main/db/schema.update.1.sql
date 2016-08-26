CREATE TABLE purchases
(
    id DOUBLE PRIMARY KEY NOT NULL AUTO_INCREMENT,
    account VARCHAR(255),
    time TINYBLOB,
    product INT(11),
    machine INT(11),
    CONSTRAINT FK_product FOREIGN KEY (product) REFERENCES products (id),
    CONSTRAINT FK_machine FOREIGN KEY (machine) REFERENCES machines (id)
);
CREATE INDEX FK_product ON purchases (product);
CREATE INDEX FK_machine ON purchases (machine);