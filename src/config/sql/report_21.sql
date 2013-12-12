select 
      u0.u_id as "U_ID",
      u0.p_id as "产品号码",  	 
 	  u0.username as "用户名",
 	  u0.user_no as "帐号",
      u0.area as "区域", 
      u0.address as "地址", 
      u0.tel as "联系方式", 
      u0.begin_date as "录入时间"  ,
      u0.del_date as "预拆机时间"  ,
      u0.finish_del as "拆机时间*"  ,
      u0.branch as "维护方式", 
      u0.ont_id as "ONT端口", 
      u0.old_p_id as "旧产品号码",	
      u0.j_id as "原J_ID",
      j.jx as "机房",
      j.sbh as "设备号",
      j.type as "AD类型",
      j.board_type as "板类型",
      j.slot as "槽号",
      j.sb_port as "端口号",
      j.mdf_port as "MDF横列",
      j.outer_vlan as "外层VLAN",
      j.inner_vlan as "内层VLAN"
from  dbo.userinfo_log u0 left join jx_info j on j.j_id=u0.j_id  
where  1=1  {0} {1} {2}  {3} {4} {5} {7} {8} {9}
order by u0.finish_del desc