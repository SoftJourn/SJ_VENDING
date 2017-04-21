# Delete unused links
DELETE images FROM images
  LEFT JOIN products ON images.product_id = products.id
WHERE products.id IS NULL;
# Add foreign key to product id
ALTER TABLE images
  MODIFY product_id INT(11);
ALTER TABLE images
  ADD FOREIGN KEY (product_id) REFERENCES products (id)
  ON DELETE CASCADE;
# Create procedure to image migration
DROP PROCEDURE IF EXISTS dump_image;
DELIMITER //
CREATE PROCEDURE dump_image()
  BEGIN
    DECLARE this_id INT;
    DECLARE productId INT DEFAULT 0;
    DECLARE cur1 CURSOR FOR SELECT
                              id,
                              product_id
                            FROM images;
    OPEN cur1;
    read_loop: LOOP
      FETCH cur1
      INTO this_id, productId;
      SET @query = concat('select data from images where id=',
                          this_id, ' into dumpfile "/var/lib/mysql-files/', this_id, '_', productId, '"');
      PREPARE write_file FROM @query;
      EXECUTE write_file;
    END LOOP;
    CLOSE cur1;
  END //
DELIMITER ;

# call procedure
CALL dump_image();

# Check where allowed to export folder location
# SHOW VARIABLES LIKE "secure_file_priv";

