--<TITLE>机房,OLT编码,分光器编码,分光器端口,产品号码,SN</TITLE>
select jx, 
'0'+substring(mdf_port,1,flj-1),
substring(mdf_port,flj+1,charindex('-',mdf_port,flj+1)-flj-1),
substring(mdf_port,charindex('-',mdf_port,flj+1)+1,5),
 p_id ,
 coalesce('''' + sn,'')
 from (
select jx,mdf_port,sn,
charindex('-',mdf_port) flj,u.p_id 
from adsl..jx_info j left join adsl..user_info u on j.j_id=u.j_id 
where j.type='FTTH' 
) a
ORDER BY jx, mdf_port
