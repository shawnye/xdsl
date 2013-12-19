select 
      j0.j_id as "J_ID",
      j0.jx as "机房", 
      j0.type as "类型",
      j0.sbh as "设备号",
      j0.slot as "槽号", 
      j0.sb_port as "设备端口", 
      j0.mdf_port as "MDF端口", 
  
      j0.board_type as "板卡类型", 
      
      j0.outer_vlan as "外层VLAN",
      j0.inner_vlan as "内层VLAN",
      j0.ip as "设备IP地址",
      
      u0.ont_id as "ONT端口", 
      j0.sn as "SN", 
      
	  u0.user_no as "帐号",
      u0.p_id as "产品号码",  	
     
 	  u0.username as "用户名",
      u0.address as "地址",  
      
      u0.begin_date as "录入时间"  ,
      
       u0.old_p_id as "旧产品号码",  	
      j0.olt as "OLT名称"
from dbo.jx_info j0 left join
      dbo.user_info u0 on j0.j_id = u0.j_id  
where (mask is null or mask = '')  {0} {1} {2}  {3} {4} {5} {7} {8} {9} {10}
