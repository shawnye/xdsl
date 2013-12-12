--NGN设备统计
SELECT 
jfmc as "机房", 
sbmc as "设备名称",
sblx as "设备类型",
c as "设备端口总数",
c1 as "设备端口占用数",
c2 as "设备反极端口总数",
c3 as "设备反极端口占用数"
 FROM OPENROWSET('SQLOLEDB','{db.linked_server1}';'{db.linked_server1.username}';'{db.linked_server1.password}',
   'SELECT jfmc,sbmc,min(sblx) sblx ,count(dkh) c,count( (case when haoma<>''空号'' then 1 else null end) ) c1,count( (case when  jixing=''yes'' then 1 else null end) ) c2,count( (case when haoma<>''空号'' and jixing=''yes'' then 1 else null end) ) c3
 FROM xiaocw1..ngn_dk where jfmc is not null group by jfmc,sbmc ') x
where 1=1 {0} {1} {2}  {3} {4} {5} {7} {8} {9}