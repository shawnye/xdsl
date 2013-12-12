select jx as "机房",
sbh as "OTL编码（设备号)", 
fen_guang as "分光编码",
count(*) as "分光端口总数" ,
count((case when used=1 then 1 else null end)) as "分光端口占用数", 
sum(ont_ports) as "ONT端口总数",
sum(used_ont_ports) as "ONT端口占用数（用户数）"  
from  
(
 select j_id, jx,sbh,mdf_port,substring(mdf_port,1,charINDEX('-',mdf_port,4)-1) fen_guang,used, ont_ports,used_ont_ports
 from jx_info where type='FTTH'  
) a where 1=1 {0} {1} {2} {3} 
group by jx,sbh, fen_guang
order by jx,sbh, fen_guang
 
 