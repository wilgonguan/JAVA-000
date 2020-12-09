-- 数据查询
DROP PROCEDURE IF EXISTS order_query;
DELIMITER $
CREATE PROCEDURE order_query()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i<=1000000 DO
        select * from test.orders where id = i;
        SET i = i+1;
    END WHILE;
END $
CALL order_query();

