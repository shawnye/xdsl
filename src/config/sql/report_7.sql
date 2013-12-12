select 
      u0.p_id as "产品号码",
      u0.username as "用户名",
      u0.address as "地址",  
      j0.jx as "机房", 
      j0.sbh as "设备号",
  
      j0.olt as "OLT名称"
	   
from dbo.jx_info j0 inner join
      dbo.user_info u0 on j0.j_id = u0.j_id  
where used=1 and (mask is null or mask = '')  {0} {1} {2}  {3} {4} {5} {7} {8} {9}
order by j0.jx, j0.sbh