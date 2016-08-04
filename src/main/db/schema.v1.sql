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