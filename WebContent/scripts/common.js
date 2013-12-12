function setValue(src, dest, append){
	if(typeof append == 'undefined'){
		append = false;
	}
	src = $('#' +src );
	dest = $('#' +dest );
	if(append){
		dest.val (dest.val() + src.val()) ;
	}else{
		dest.val (src.val()) ;
	}

}
function delayStatus(obj, enable, delayMillis){
	setTimeout(function(){
		if(enable){
			$(obj).attr("disabled","");
		}else{
			$(obj).attr("disabled","disabled");
		}
	}, delayMillis);
}

function changePassword(a){
	var np = window.prompt('请输入新密码（空白则取消修改，注：不能填写中文）：','');
	if(!np){
		return false;
	}
	a.href = a.href + "?np=" + np;
	
	return true;
	
}

//-----------------------------------------------------------
function removeAll(select){
	while(select.options.length > 0){
			//select.options.remove(0);//FF不支持
			select.options[0] = null;
	}
}

/**
* select
* options 列表，每个列表元素包括许多属性，其中1-2个属性用于显示和返回值
* lableProperty: 显示字段
* valuePropery: 值字段
* append:false
*/
function renderSelect(select,options, lableProperty, valuePropery, append, withSeq, reservedValues/*deprecated*/){
//alert('renderSelect...' + select.nodeName + "," + options.length);
	if(typeof append == 'undefined'){
		append = false;
	}

	if(typeof withSeq == 'undefined'){
		withSeq = false;
	}

	if(typeof reservedValues == 'undefined'){
		reservedValues = [];
	}
	
	if(!append){
		//remove all

			var reservedOptions = [];
			for ( var i = 0; i < reservedValues.length; i++) {
				if(!reservedValues[i]){
					continue;
				}
				//search reservedValues[i] in select.options 
				for ( var j = 0; j < select.options.length; j++) {
					if(reservedValues[i] == select.options[j].value){
						reservedOptions.push(select.options[j]);
						break;
					}
				}
				 
				
			}
			
			removeAll(select, reservedValues);
			select.options.add(new Option('请选择',''));
			for ( var k = 0; k < reservedOptions.length; k++) {
				select.options.add(reservedOptions[k]);
			}
		}
	if(options == null || !options.length){
		return;
	}

	for(var i=0; i < options.length ; i++){
		if(withSeq){
			select.options.add(new Option((i+1) + '.' +  options[i][lableProperty],options[i][valuePropery]));
		}else{
			select.options.add(new Option(options[i][lableProperty],options[i][valuePropery]));
		}
	}
}


function renderSelect2(select,options, lableFunc, valuePropery, append,   reservedValues/*deprecated*/){
	//alert('renderSelect...' + select.nodeName + "," + options.length);
		if(typeof append == 'undefined'){
			append = false;
		}

		 
		
		if(typeof reservedValues == 'undefined'){
			reservedValues = [];
		}

		if(!append){
		//remove all

			var reservedOptions = [];
			for ( var i = 0; i < reservedValues.length; i++) {
				//search reservedValues[i] in select.options 
				for ( var j = 0; j < select.options.length; j++) {
					if(reservedValues[i] == select.options[j].value){
						reservedOptions.push(select.options[j]);
						break;
					}
				}
				 
				
			}
			
			removeAll(select, reservedValues);
			select.options.add(new Option('请选择',''));
			for ( var k = 0; k < reservedOptions.length; k++) {
				select.options.add(reservedOptions[k]);
			}
		}

		if(options == null || !options.length){
			return;
		}

		for(var i=0; i < options.length ; i++){
			select.options.add(new Option(lableFunc(options[i]) ,options[i][valuePropery]));

		}
	}