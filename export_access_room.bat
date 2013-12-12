rem example

@echo off
set EXP_DIR=%~dp0\export

echo 导出接入间信息...
rem  sql语句出现 ‘%’需要转义：%%
bcp "SELECT   j.j_id, jx AS 机房, sbh AS 设备号, slot AS 槽号,        sb_port AS 端口号, board_type AS 板卡类型, outer_vlan AS 外层VLAN,        inner_vlan AS 内层VLAN, mdf_port AS 横列端口,COALESCE (u.u_id,-1) u_id, coalesce('''' + u.p_id,'') 产品号码,coalesce( u.username,'') 客户名称,coalesce(user_no,'') 账号,coalesce( address,'') 地址 FROM adsl..jx_info j left join adsl..user_info u on j.u_id=u.u_id WHERE 1=1 and jx in( '江门丹井里', '江门白石', '鹤山中东西', '江门步岭', '江门潮莲卢边', '鹤山中东西DJ0501EPON', '江门白石DJ0401EPON', '江门白石DJ0601EPON', '江门白石DJ0701EPON', '江门白石DJ1201EPON', '江门丹井DJ0201EPON', '江门丹井DJ0701EPON', '江门丹井DJ0702EPON', '江门丹井DJ1201EPON', '鹤山中东西DJ0301EPON', '江门白石DJ1301EPON', '江门白石营业厅', '江门邦德', '江门滨江大道', '江门长乔'  ) ORDER BY jx, sbh, slot, sb_port " queryout "%EXP_DIR%\接入间信息.csv"  -c  -t , -r \n  -SGDCYEXY6 -Uyexy6 -Pcncjm'123


pause