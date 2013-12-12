CREATE FUNCTION [dbo].[createNMport](
	@sbh varchar(100),
	@ip varchar(100),
	@slot varchar(100),
	@port varchar(100)
)
 returns varchar(100)
as
 begin
	declare @rt  varchar(256) 
	declare @left4  varchar(4) 
	declare @left5  varchar(5) 
	declare @left6  varchar(6) 
	declare @left9  varchar(9) 
	declare @left10  varchar(10) 
	set @rt='��'

	if (@ip=null or @ip ='')
	begin
	 return 'IPΪ��'
	end
	
	set @left4=left(@sbh,4)
	set @left5=left(@sbh,5)
	set @left6=left(@sbh,6);
	set @left9=left(@sbh,9)
	set @left10=left(@sbh,10);
 
	if(@left5 = '9806H')
	begin
		--�豸IP�ո�ADSL�ո�ۺ�/�˿ں�
		set @rt = @ip + ' ADSL ' + @slot + '/' + @port 
		 
	end
	else
	if(@left6 = '7302FD')
	begin
	   --�豸IP�ո�1/1/�ۺ�/�˿ں�
		set @rt = @ip + ' 1/1/' + @slot + '/' + @port  
	end
	else
	if(@left6 = 'MA5100' or @left6 = 'MA5103')
	begin
	   --�豸IP�ո�Frame�ո�0�ո�Card�ո�ۺſո�Port�ո�˿ں�
		set @rt = @ip + ' Frame 0 Card ' + @slot + ' Port ' + @port  
	end
	
	else
	if(@left6 = 'MA5105')
	begin
		--�豸IP�ո�ADSL:0/�ۺ�/�˿ں�
		set @rt = @ip + ' ADSL:0/' + @slot + '/' + @port  
	end

	else
	if(@left6 = 'MA5600' or @left6 = 'MA5603' or @left10 = 'UA5000ipmB')
	begin
	    --�豸IP�ո�adsl0/�ۺ�/�˿ں�
		set @rt = @ip + ' adsl0/' + @slot + '/' + @port  
	end
	
	else
	if( @left9= 'UA5000ipm')
	begin
	   --�豸IP�ո�0/�ۺ�/1/�˿ں�
		set @rt = @ip + ' 0/' + @slot + '/1/' + @port  
	end
	 
	else
	if(@left6 = 'MA5605' )
	begin
	   --�豸IP�ո�ADSL:0/�ۺ�/�˿ں�
		set @rt = @ip + ' ADSL:0/' + @slot + '/' + @port  
	end

	else
	if(@left4 = 'F822' )
	begin
		--�豸IP�ո�Ethernet�ո�ۺ�/�˿ں�
		set @rt = @ip + ' Ethernet ' + @slot + '/' + @port  
	end

	else
	if(@left4 = 'F820' )
	begin
		--�豸IP�ո�fei_0/�ۺ�/�˿ں�
		set @rt = @ip + ' fei_0/' + @slot + '/' + @port  
	end
	
 return  @rt
end

