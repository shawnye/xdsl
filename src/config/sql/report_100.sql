select jx as "机房",
      username as "用户名称",
       p_id as "产品号",
      tel as "联系方式" ,
      user_no as "OSS帐号",
      bss_user_no as "BSS帐号"
from user_info u , jx_info j
where u.u_id=j.u_id {0} {1} {2}