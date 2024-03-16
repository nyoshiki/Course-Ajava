INSERT INTO flower_db.sales (sales_no, sales_date, sales_date_time, total_qty, total_amount) VALUES('F20200401102000',  '20200401', '20200401092000', 4, 1600); 
INSERT INTO flower_db.sales_detail (sales_detail_no, sales_no, product_id, product_name, unit_price, qty, amount) VALUES('F20200401102000-001', 'F20200401102000', 'FW00000010', 'バラ', 500, 2, 1000); 
INSERT INTO flower_db.sales_detail (sales_detail_no, sales_no, product_id, product_name, unit_price, qty, amount) VALUES('F20200401102000-002', 'F20200401102000', 'FW00000020', 'ガーベラ', 300, 2, 600); 
 
SELECT * FROM flower_db.sales; 
SELECT * FROM flower_db.sales_detail;
