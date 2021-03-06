
select 
users as "帐号",
branch as "名称（单位）",
m_level as "操作级别" ,
(select max(r.name) from Role r where r.code= roles) as "角色",
state as "状态" ,
ip as "最近登录IP" ,
logon_time as "最近登录时间" ,
(case
	when state=1 and logon_time is not null then cast((getdate()-logon_time) as integer)
	else null
end) as "未登陆时间(日)"

from Manager m 
where 1=1 {0} {1} {2}  {3} {4}

order by state desc, roles,users
 