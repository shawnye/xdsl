--utf-8编码！直接修改生效。 <TITLE>j_id,机房,产品类型,设备号,设备IP,槽号,端口号,网管设备端口,板卡类型,OLT名称,外层VLAN,内层VLAN,横列端口,是否占用,ONT端口总数,ONT端口已占用数,ONT端口号,SN,u_id,产品号码,旧产品号码,帐号,状态,录入时间</TITLE>
SELECT j.j_id, jx ,type, sbh ,ip, slot , sb_port , dbo.createNMport(sbh, ip,slot,sb_port),
      board_type, olt, outer_vlan ,
      inner_vlan , mdf_port ,j.used ,j.ont_ports, j.used_ont_ports, 
      u.ont_id, coalesce('''' + j.sn,''), COALESCE (u.u_id,-1) , coalesce('''' + u.p_id,''), coalesce('''' + u.old_p_id,''), rtrim(u.user_no),  u.state , begin_date
FROM adsl..jx_info j left join adsl..user_info u on j.j_id=u.j_id
where (mask is null or mask = '') 
ORDER BY jx, sbh, slot, sb_port

