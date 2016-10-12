ALTER TABLE machines DROP address;
CREATE UNIQUE INDEX machines_name_uindex ON machines (name);