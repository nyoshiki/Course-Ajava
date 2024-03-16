DROP TABLE IF EXISTS flower_db.sales;
DROP TABLE IF EXISTS flower_db.sales_detail;

CREATE TABLE flower_db.sales( 
  sales_no varchar(15) not null primary key, 
  sales_date varchar(8) not null, 
  sales_date_time varchar(14) not null,
  total_qty int not null,
  total_amount int not null 
); 
 
CREATE TABLE flower_db.sales_detail( 
  sales_detail_no varchar(20) not null primary key, 
  sales_no varchar(15) not null,  
  product_id varchar(20) not null, 
  product_name varchar(50) not null, 
  unit_price int not null, 
  qty int not null, 
  amount int not null 
); 
 
SHOW TABLES;