select j.j_id as "J_ID", j.jx as "机房", j.sbh as "设备号", j.type as "类型",
      j.slot as "槽号", j.sb_port as "设备端口", j.mdf_port as "MDF端口", 
      j.board_type as "板卡类型",  j.inner_vlan as "内层VLAN",
      j.outer_vlan as "外层VLAN",j.ont_ports as "ONT总端口数",j.used_ont_ports as "已占用ONT端口数"
from dbo.jx_info j  
where (mask is null or mask = '') and (j.used=0 or (j.used=1 and j.ont_ports>j.used_ont_ports )) {0} {1}  {2}  {3} {4} {5}
order by j.jx, j.sbh, j.slot, j.sb_port