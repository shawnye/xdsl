--<TITLE>j_id,机房,产品类型,设备号,设备IP,槽号,端口号,板卡类型,OLT名称,外层VLAN,内层VLAN,横列端口,ONT端口总数,ONT端口已占用数,ONT端口号,SN,u_id,产品号码,旧产品号码,客户名称,账号,地址</TITLE>
SELECT j.j_id, jx ,type , sbh , ip, slot ,
      sb_port , board_type, olt, outer_vlan ,
      inner_vlan , mdf_port,j.ont_ports, j.used_ont_ports, ont_id, coalesce('''' + sn,''), 
      COALESCE (u.u_id,-1) , coalesce('''' + u.p_id,''),coalesce('''' + u.old_p_id,''),coalesce( u.username,'') ,rtrim(user_no) ,coalesce( address,'') 
FROM adsl..jx_info j left join adsl..user_info u on j.j_id=u.j_id
WHERE (mask is null or mask = '') and jx in(
{0}
)
ORDER BY jx, sbh, slot, sb_port

