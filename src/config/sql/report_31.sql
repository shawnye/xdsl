SELECT 
jfmc as "机房", 
haoma as "号码",
jixing as "是否反极",
hl as "横列*",
kh as "框号",
cast(ch as int) as "槽号",
cast(dkh as int) as "端口号",

sblx as "设备类型"

 
 FROM OPENROWSET('SQLOLEDB','{db.linked_server1}';'{db.linked_server1.username}';'{db.linked_server1.password}',
   'SELECT * FROM xiaocw1..ngn_dk ') a
where 1=1 {0} {1} {2}  {3} {4} {5} {7} {8} {9}