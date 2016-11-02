ALTER TABLE machines ADD unique_id VARCHAR(36) NOT NULL;
CREATE UNIQUE INDEX machines_unique_id_uindex ON sj_vending.machines (unique_id);

ALTER TABLE products CHANGE addedTime added_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products CHANGE imageData image_data MEDIUMBLOB;