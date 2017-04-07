DELETE images FROM images
  LEFT JOIN products ON images.product_id = products.id
WHERE products.id IS NULL;
ALTER TABLE images
  MODIFY product_id INT(11);
ALTER TABLE images
  ADD FOREIGN KEY (product_id) REFERENCES products (id)
  ON DELETE CASCADE;