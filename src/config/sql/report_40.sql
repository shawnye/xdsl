select 
      
      j0.jx as "机房", 
      j0.sbh as "设备号",
       
      j0.used as "是否占用", 
      j0.ip as "IP地址", 
  
      j0.type as "AD端口类型", 
       
      u0.p_id as "产品号码",  	
      u0.old_p_id as "旧产品号码",  	
 	  u0.username as "用户名",
      u0.address as "地址",
      u0.state as "状态"

from dbo.jx_info j0 left join
      dbo.user_info u0 on j0.u_id = u0.u_id  
where u0.state not in ('未分配','预分配','预拆机','已拆机') {0} {1} {2}  {3} {4} {5} {7} {8} {9}
