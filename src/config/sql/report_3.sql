  
select 
type as "类型",
(select a.area from area_jx a where a.jx=k.jx) as "区域",
k.jx as "机房",
k.sbh2 as "设备型号",
count(k.sbh2) as "数量",
sum(total) as "端口总数",
sum(used_ports) as "已用端口数",
sum(unused_ports) as "可用端口数",
sum(bad_ports) as "坏端口"
from 
(
select 
j.type ,

j.jx ,
j.sbh ,
(case 
when  charindex('ipm',sbh)>0 then substring(sbh,0,charindex('ipm',sbh))  --优先
when charindex('_',sbh)>0 then  substring(sbh,0,charindex('_',sbh)) 
when  charindex('-',sbh)>0 then substring(sbh,0,charindex('-',sbh))  
else sbh end) sbh2,

count(*)  total,
count( case when j.used=1 then 1 else null end ) used_ports   ,
count( case when j.used=0 then 1 else null end ) as unused_ports  ,
count( case when j.used is null then 1 else null end ) bad_ports 
 
from jx_info j
where 1=1 {0} {1} {2} {3}
group by j.type, j.jx , j.sbh

) k

group by k.type, k.jx , k.sbh2
order by k.type, k.jx , k.sbh2