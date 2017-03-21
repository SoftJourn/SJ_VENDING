-- noinspection SqlResolveForFile

INSERT INTO categories (name) VALUES ('Drink');
INSERT INTO categories (name) VALUES ('Lemonades');
INSERT INTO categories (name) VALUES ('New');
INSERT INTO categories (name) VALUES ('Only');
INSERT INTO categories (name) VALUES ('Other stuff');
INSERT INTO categories (name) VALUES ('Sandwiches');
INSERT INTO categories (name) VALUES ('Snack');
INSERT INTO categories (name) VALUES ('Sugar');

INSERT INTO products (id, image_url, "NAME", price, added_time, description, image_data, id_categories)
  VALUES (0, 'terasdfgh.jpg', 'COCA', 10, CURRENT_TIMESTAMP, 'adfgsfg', NULL , 1);