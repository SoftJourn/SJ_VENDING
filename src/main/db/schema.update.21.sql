UPDATE images
SET url = CONCAT('products/', images.product_id, '/images/', images.id, '.', images.resolution);
CREATE UNIQUE INDEX unique_url
  ON images (url);