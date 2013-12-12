--ports_stat
select  j.jx as "机房",j.sbh as "设备", count(distinct slot) as "板卡数",count(*) as "端口数", 
count( case when j.used=1  then 1 else null end ) as "已用端口数"  ,
count( case when j.used=0  then 1 else null end ) as "未用端口数"  ,
count( case when j.used is null  then 1 else null end ) as "坏端口数"  ,

count(*) /count(distinct slot) as "平均端口数/板卡"
from jx_info j
where 1=1 {0} {1}  {2}  {3}
group by j.jx, j.sbh
order by j.jx, j.sbh