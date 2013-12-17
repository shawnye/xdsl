select j.j_id as "J_ID", j.jx as "机房", j.sbh as "设备号", j.type as "类型",
      j.slot as "槽号", j.sb_port as "设备端口", j.mdf_port as "MDF端口", 
      j.board_type as "板卡类型",  j.inner_vlan as "内层VLAN",
      j.outer_vlan as "外层VLAN",j.ont_ports as "ONT总端口数", 
      j.used_ont_ports as "已占用ONT端口数(应为0)",
      j.used as "是否占用（应为否）",
      j.mask as "占用标识"
from dbo.jx_info j  
where (mask is not null and mask <> '') {0} {1}  {2}  {3} {4} {5}
order by j.jx, j.sbh, j.slot, j.sb_port
