ALTER TABLE image ADD is_cover BOOLEAN DEFAULT FALSE ;
ALTER TABLE image ADD url VARCHAR(255) DEFAULT '' ;
INSERT INTO image (image.data,image.product_id,image.resolution,image.is_cover)
  SELECT image_data,id,'jpeg',TRUE from products;
UPDATE image
SET url = CONCAT ('products/', image.product_id,'/images/',image.id,'.',image.resolution);

ALTER TABLE products DROP COLUMN image_url;
ALTER TABLE products DROP COLUMN image_urls;
ALTER TABLE products DROP COLUMN image_data;

ALTER TABLE image RENAME images;