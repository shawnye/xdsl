select u.u_id as "U_ID", u.p_id as "产品号码",u.username as "用户名", 
	   u.ont_id as "ONT端口号",u.begin_date as "录入时间",
	   u.branch as "维护单位",
	  j.j_id as "J_ID", j.jx as "机房", j.sbh as "设备号", 
      j.slot as "槽号", j.sb_port as "设备端口", j.mdf_port as "MDF端口" 
       
from dbo.user_info u, dbo.jx_info j  
where u.j_id=j.j_id and j.type='FTTH' and (j.sn='' or j.sn is null) and (mask is  null or mask = '')  {0} {1}  {2}  {3} {4} {5}
order by u.begin_date desc
