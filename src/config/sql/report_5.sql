select 
event as "操作类型",
remark as "操作内容",
now_time as "操作时间" ,
users as "操作人帐号" ,
(select m.branch from Manager m where m.users=l.users) as "修改人名称"

from log l
where 1=1 {0} {1} {2}  {3} {4}
order by id  desc