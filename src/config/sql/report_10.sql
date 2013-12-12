  
select
u_id as "U_ID", 
p_id  as "产品号码(修改时)",
username  as "用户名称", 
old_j_id as "原J_ID",
(select jx from jx_info j where old_j_id=j_id) as "原机房",
(select sbh from jx_info j where old_j_id=j_id) as "原设备号",
(select slot from jx_info j where old_j_id=j_id) as "原槽号",
(select sb_port from jx_info j where old_j_id=j_id) as "原设备端口",
(select mdf_port from jx_info j where old_j_id=j_id) as "原MDF端口",
old_ont_id as "原ONT端口",
new_j_id as "新J_ID",
(select jx from jx_info j where new_j_id=j_id) as "新机房",
(select sbh from jx_info j where new_j_id=j_id) as "新设备号",
(select slot from jx_info j where new_j_id=j_id) as "新槽号",
(select sb_port from jx_info j where new_j_id=j_id) as "新设备端口",
(select mdf_port from jx_info j where new_j_id=j_id) as "新MDF端口",
new_ont_id as "新ONT端口",


remark as "备注", 
change_time as "修改时间", 
changer as "修改人帐号",
(select m.branch from Manager m where m.users=changer) as "修改人名称"
from port_change_hist p
where 1=1 {0} {1} {2} {3}
order by change_time desc