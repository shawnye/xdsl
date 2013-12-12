select j.jx as "机房" , COALESCE(max(j.olt),'--') as "OLT名称" 
 
from jx_info j
where 1=1 {0} {1} {2}  {3} 
group by j.jx 
order by j.jx 
