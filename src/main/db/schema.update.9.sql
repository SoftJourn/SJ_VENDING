CREATE TABLE load_history
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  price DECIMAL(19,2) NOT NULL,
  date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  is_distributed TINYINT(1) DEFAULT 0 NOT NULL,
  machine_id INT(11) NOT NULL,
  CONSTRAINT load_history_machines_id_fk FOREIGN KEY (machine_id) REFERENCES machines (id)
);