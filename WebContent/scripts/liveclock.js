/*
*实时显示时间.
*/
var weekdaystxt=["日", "一", "二", "三", "四", "五", "六"];

function LiveClock(containerId, showDate, h24){
	if (!document.getElementById || !document.getElementById(containerId)) return;
	this.container=document.getElementById(containerId);
	
	this.showDate = showDate;//boolean,显示日期?
	this.h24=h24;//boolean:24小时制?
	
	this.localtime=new Date();//client time
	
	this.updateTime();
	this.updateContainer();
}
LiveClock.prototype.updateTime=function(){
	var thisobj=this;
	this.localtime.setSeconds(this.localtime.getSeconds()+1);
	setTimeout(function(){thisobj.updateTime()}, 1000); //update time every second
}


LiveClock.prototype.updateContainer=function(){
	var thisobj=this;
	
	var hour=this.localtime.getHours();
	var minutes=this.localtime.getMinutes();
	var seconds=this.localtime.getSeconds();
	var dayofweek=weekdaystxt[this.localtime.getDay()];
	var content = '';
	if(this.showDate){
		var year=this.localtime.getFullYear();
		var month=this.localtime.getMonth()+1;
		var date=this.localtime.getDate();
		
		content = year+"-"+formatField(month)+"-"+formatField(date)+" ";
	}
	
	if(this.h24){
		content += formatField(hour)+":"+formatField(minutes)+":"+formatField(seconds)+"(周"+dayofweek+")";
	
	}else{
		var ampm=(hour>=12)? "下午" : "上午";
		content += formatField(hour, 1)+":"+formatField(minutes)+":"+formatField(seconds)+" "+ampm+"(周"+dayofweek+")";
	}

	this.container.innerHTML=content;
	
	setTimeout(function(){thisobj.updateContainer()}, 1000) //update container every second
}


function formatField(num, h12){
	if (typeof h12!="undefined"){ //if this is the hour field
		var hour=(num>12)? num-12 : num;
		return (hour==0)? 12 : hour;
	}
	return (num<=9)? "0"+num : num//if this is minute or sec plus mon, date field
}