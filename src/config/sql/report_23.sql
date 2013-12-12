select 
      
      j0.jx as "机房", 
      
      j0.sbh as "设备号",
      j0.type as "类型",
      
      j0.slot as "槽号", 
      j0.sb_port as "设备端口号", 
      j0.mdf_port as "MDF端口号", 
      j0.used as "是否占用", 
  
      j0.board_type as "板卡类型", 
       
      j0.inner_vlan as "内层VLAN",
      j0.outer_vlan as "外层VLAN",
      j0.ip as "IP地址",
      j0.olt as "OLT名称",

      j0.ont_ports as "ONT端口总数", 
      j0.j_id as "机房端口号(J_ID)"
      
from dbo.jx_info j0   
where (mask is null or mask = '')  {0} {1} {2}  {3} {4} {5} {7} {8} {9}
