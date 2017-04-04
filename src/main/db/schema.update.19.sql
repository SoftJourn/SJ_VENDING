drop procedure if exists dump_image;
delimiter //
create procedure dump_image()
  begin
    declare this_id int;
    declare productId int default 0;
    declare cur1 cursor for select id, product_id from images;
    open cur1;
    read_loop: loop
      fetch cur1 into this_id,productId;
      set @query = concat('select data from images where id=',
                          this_id, ' into dumpfile "/var/lib/mysql-files/',this_id,'_',productId,'"');
      prepare write_file from @query;
      execute write_file;
    end loop;
    close cur1;
  end //
delimiter ;

call dump_image();

# SHOW VARIABLES LIKE "secure_file_priv";