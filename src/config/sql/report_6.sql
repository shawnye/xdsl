select j0.j_id as "J_ID", j0.jx as "机房", j0.sbh as "设备号", j0.type as "类型",
      j0.slot as "槽号", j0.sb_port as "设备端口", j0.mdf_port as "MDF端口", j0.used as "是否占用",
      j0.board_type as "板卡类型", j0.ip as "机房IP", j0.inner_vlan as "内层VLAN",
      j0.outer_vlan as "外层VLAN", olt as "OLT名称",u0.u_id as "U_ID",
      u0.username as "用户名称", u0.p_id as "产品号码",u0.old_p_id as "旧产品号码", u0.address as "地址",
      u0.user_no as "OSS帐号", u0.area as "区域",
      u0.begin_date as "开始时间", u0.finish_date as "竣工时间", u0.state as "端口状态",
      u0.remark as "备注"
 from jx_info j0 inner join
(
select  j.u_id  
         from jx_info j 
	where j.u_id is not null
         group by j.u_id
         having count(*) > 1
) a on a.u_id=j0.u_id left join user_info u0 on a.u_id=u0.u_id
order by a.u_id