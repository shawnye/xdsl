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
	set @rt='无'

	if (@ip=null or @ip ='')
	begin
	 return 'IP为空'
	end
	
	set @left4=left(@sbh,4)
	set @left5=left(@sbh,5)
	set @left6=left(@sbh,6);
	set @left9=left(@sbh,9)
	set @left10=left(@sbh,10);
 
	if(@left5 = '9806H')
	begin
		--设备IP空格ADSL空格槽号/端口号
		set @rt = @ip + ' ADSL ' + @slot + '/' + @port 
		 
	end
	else
	if(@left6 = '7302FD')
	begin
	   --设备IP空格1/1/槽号/端口号
		set @rt = @ip + ' 1/1/' + @slot + '/' + @port  
	end
	else
	if(@left6 = 'MA5100' or @left6 = 'MA5103')
	begin
	   --设备IP空格Frame空格0空格Card空格槽号空格Port空格端口号
		set @rt = @ip + ' Frame 0 Card ' + @slot + ' Port ' + @port  
	end
	
	else
	if(@left6 = 'MA5105')
	begin
		--设备IP空格ADSL:0/槽号/端口号
		set @rt = @ip + ' ADSL:0/' + @slot + '/' + @port  
	end

	else
	if(@left6 = 'MA5600' or @left6 = 'MA5603' or @left10 = 'UA5000ipmB')
	begin
	    --设备IP空格adsl0/槽号/端口号
		set @rt = @ip + ' adsl0/' + @slot + '/' + @port  
	end
	
	else
	if( @left9= 'UA5000ipm')
	begin
	   --设备IP空格0/槽号/1/端口号
		set @rt = @ip + ' 0/' + @slot + '/1/' + @port  
	end
	 
	else
	if(@left6 = 'MA5605' )
	begin
	   --设备IP空格ADSL:0/槽号/端口号
		set @rt = @ip + ' ADSL:0/' + @slot + '/' + @port  
	end

	else
	if(@left4 = 'F822' )
	begin
		--设备IP空格Ethernet空格槽号/端口号
		set @rt = @ip + ' Ethernet ' + @slot + '/' + @port  
	end

	else
	if(@left4 = 'F820' )
	begin
		--设备IP空格fei_0/槽号/端口号
		set @rt = @ip + ' fei_0/' + @slot + '/' + @port  
	end
	
 return  @rt
end

