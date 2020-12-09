DROP PROCEDURE if exists insertdata;
delimiter //
CREATE PROCEDURE insertdata()
begin
declare yourid int;
set yourid = 1;
while yourid <= 100000 do
  insert into mydb.order(id, name)
  values(yourid, '张三'), (yourid+1, '张三'), (yourid+2, '张三'), (yourid+3, '张三'), (yourid+4, '张三'),
  (yourid+5, '张三'), (yourid+6, '张三'), (yourid+7, '张三'), (yourid+8, '张三'), (yourid+9, '张三');
  set yourid=yourid+10;
end while;
end//
delimiter ;
call insertdata();

DROP PROCEDURE if exists insertdata;
delimiter //
CREATE PROCEDURE insertdata()
begin
declare yourid int;
set yourid = 1;
while yourid <= 100000 do
  insert into mydb.order(id, name)
  values(yourid, '张三');
  set yourid=yourid+1;
end while;
end//
delimiter ;
call insertdata();
