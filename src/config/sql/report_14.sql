
SELECT [old_jx]  as "原机房",
[old_sbh]  as "原设备号",
[old_mdf_port]  as "原MDF端口",
[new_mdf_port]  as "新MDF端口",
[new_sbh]  as "新设备号",
[new_jx]  as "新机房",
[account_or_pid]  as "账号或产品号",
[phone_mdf_port]  as "语音横列",
[col_mdf_port]  as "直列",
[remark]  as "备注",
[batch_num]  as "导入批次",
[create_time]  as "创建时间",
[creater]  as "创建人",
[old_j_id]  as "原J_ID",
[new_j_id]  as "新J_ID",
[u_id]  as "U_ID",
[updated] as "是否更新成功"
FROM [dbo].[cutover]
where 1=1 {0} {1}  {2}  {3} {4} {5} {6} {7} {8} {9}
order by batch_num desc