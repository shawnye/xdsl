rem example

@echo off
set EXP_DIR=%~dp0\export

echo �����������Ϣ...
rem  sql������ ��%����Ҫת�壺%%
bcp "SELECT   j.j_id, jx AS ����, sbh AS �豸��, slot AS �ۺ�,        sb_port AS �˿ں�, board_type AS �忨����, outer_vlan AS ���VLAN,        inner_vlan AS �ڲ�VLAN, mdf_port AS ���ж˿�,COALESCE (u.u_id,-1) u_id, coalesce('''' + u.p_id,'') ��Ʒ����,coalesce( u.username,'') �ͻ�����,coalesce(user_no,'') �˺�,coalesce( address,'') ��ַ FROM adsl..jx_info j left join adsl..user_info u on j.u_id=u.u_id WHERE 1=1 and jx in( '���ŵ�����', '���Ű�ʯ', '��ɽ�ж���', '���Ų���', '���ų���¬��', '��ɽ�ж���DJ0501EPON', '���Ű�ʯDJ0401EPON', '���Ű�ʯDJ0601EPON', '���Ű�ʯDJ0701EPON', '���Ű�ʯDJ1201EPON', '���ŵ���DJ0201EPON', '���ŵ���DJ0701EPON', '���ŵ���DJ0702EPON', '���ŵ���DJ1201EPON', '��ɽ�ж���DJ0301EPON', '���Ű�ʯDJ1301EPON', '���Ű�ʯӪҵ��', '���Ű��', '���ű������', '���ų���'  ) ORDER BY jx, sbh, slot, sb_port " queryout "%EXP_DIR%\�������Ϣ.csv"  -c  -t , -r \n  -SGDCYEXY6 -Uyexy6 -Pcncjm'123


pause