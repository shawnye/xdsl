--PSTN板卡统计
SELECT 
jfmc as "机房", 
ch as "板卡数",

c as "机房端口总数",
c1 as "机房端口占用数",
c2 as "机房反极端口总数",
c3 as "机房反极端口占用数"
 FROM OPENROWSET('SQLOLEDB','{db.linked_server1}';'{db.linked_server1.username}';'{db.linked_server1.password}',
   'SELECT jfmc,count(distinct ch) ch,count(dkh) c ,count( (case when dhhm<>''空号'' then 1 else null end) ) c1, count( (case when  fj=''是'' then 1 else null end) ) c2,count( (case when dhhm<>''空号'' and fj=''是'' then 1 else null end) ) c3 FROM xiaocw1..zytj where jfmc is not null group by jfmc ') x
where 1=1 {0} {1} {2}  {3} {4} {5} {7} {8} {9}