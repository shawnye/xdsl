--<TITLE>机房,产品号码,旧产品号码,客户名称</TITLE>
SELECT  jx ,  coalesce('''' + u.p_id,''),coalesce('''' + u.old_p_id,''),coalesce( u.username,'') 
FROM adsl..jx_info j left join adsl..user_info u on j.j_id=u.j_id
WHERE (mask is null or mask = '') and jx in(
{0}
)
ORDER BY jx, sbh, slot, sb_port

