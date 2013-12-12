SELECT 
jfmc as "机房", 
dhhm as "号码",
fj as "是否反极",
cast(cdh as int) as "场地号",
cast(mkh as int) as "模块号",
cast(sbh as int) as "设备号",
cast(kh as int) as "框号",
cast(ch as int) as "插槽号",
cast(dkh as int) as "端子号",
[横列编码] as "横列编码*"

 FROM OPENROWSET('SQLOLEDB','{db.linked_server1}';'{db.linked_server1.username}';'{db.linked_server1.password}',
   'SELECT a.*,b.* FROM xiaocw1..zytj a left join (select * from xiaocw1..oss_pstn o left join  xiaocw1..room_mapping r on r.oss =o.[接入间名称] ) b on a.jfmc= b.zytj and a.mkh =b.模块号 and a.sbh=b.设备号') 
where 1=1 {0} {1} {2}  {3} {4} {5} {7} {8} {9}