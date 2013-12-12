--数据专业设备端口资料查询
SELECT 
 d.[d_id] as "D_ID",
 [jifang] as "机房",
 [d_name] as "设备名称",
 [d_type] as "设备类型",
 [d_ip] as "设备ip地址",
 [slot] as "槽号",
 [b_type] as "板类型",
 [port] as "设备端口",
 [port_type] as "端口类型",
 [used] as "使用情况",
 [p_num] as "产品号码",
 [u_name] as "客户名称",
 [state] as "状态",
 [u_type] as "用户性质",
 [s_type] as "业务类型",
 [vid] as "VLAN", 
 [u_ip] as "用户ip地址",
 [u_gateway] as "网关",
 [u_mask] as "掩码",
 [g_dev] as "网关设备",
 [begin_date] as "开通时间",
 [contact] as "联系人",
 [tel] as "联系电话",
 [u_address] as "用户装机地址 ",
 [bandwidth] as "用户带宽(M)",
 [remark] as "备注"

 FROM [数据组].[dbo].[dev_info] d left join [数据组].[dbo].[u_info] u on d.d_id=u.d_id
 where 1=1 {0} {1} {2}  {3} {4} {5} {7} {8} {9}
 order by d.jifang, d.d_name