var guuid = "";
var deviceCnt = 0;

var init = function(){
	Native.getPrefData('key_devices', function(res){
		setDeviceList(res);
	});
};

var setDeviceList = function(res){
	var json = $.parseJSON(res);
	var strBuf = "";

	guuid = json.guuid;
	var devices = json.devices;
	deviceCnt = devices.length;

	if(devices.length > 0){
		$('.no_item').addClass('hidden_option');
		for(var i=0; i<devices.length;i++){
			var device = devices[i];
			strBuf += addDevice(device);
		}
	}
	else{
		$('.no_item').removeClass('hidden_option');
	}

	$('#device_list').html(strBuf);
	$('ul').on('click', 'li', function(){		
		var index = $("li").index(this);
   		var subid = $("li:eq(" + index + ")").attr('data-subid');
   		var type = $("li:eq(" + index + ")").attr('data-type');
   		var value = $("li:eq(" + index + ")").attr('data-value');
		Native.deviceControl(guuid, subid, type,  (value == "on") ? "off" : "on");
	});
};

var addDevice = function(device){
	var strBuf = "";
	strBuf += '<li class="b_t_b_horizontal_bg device_item ' + device.subid  + '" data-subid="' + device.subid 
				+ '" data-type="' + device.type + '" data-value="' + device.value + '">';
	strBuf += '	<div class="b_t_b_horizontal_items">';
	strBuf += '		<div id="type" class="b_t_b_horizontal_item float_left ' + getIcon(device.type, device.value) + '"></div>';
	strBuf += '		<div class="b_t_b_horizontal_item float_left text_item_padding">';
	strBuf += '			<div class="text_style_01">이름</div>';
	strBuf += '			<div class="text_style_02">' + device.name + '</div>';
	strBuf += '		</div>';
	strBuf += '		<div class="b_t_b_horizontal_item float_right text_item_padding">';
	strBuf += '			<div class="text_style_03">상태</div>';
	strBuf += '			<div id="state" class="text_style_02">' + getValue(device.type, device.value) + '</div>';
	strBuf += '		</div>';
	strBuf += '	</div>';
	strBuf += '</li>';
	return strBuf;
}

var getIcon = function(typeStr, value){
	var type = parseInt(typeStr);
	
	if(type >= 1 && type <= 5){
		if(value != 'on'){
			if(type == 5)
				return 'icon_led_all_off'
			else
				return 'icon_led_off'
		}
		else if(type == 1)
			return 'icon_led_red';		
		else if(type == 2)
			return 'icon_led_green';
		else if(type == 3)
			return 'icon_led_blue';
		else if(type == 4)
			return 'icon_led_yellow';
		else if(type == 5)
			return 'icon_led_all';
	}
};

var getValue = function(typeStr, value){
	var type = parseInt(typeStr);

	if(type >= 1 && type <= 5){
		if(value != 'on')
			return '꺼짐';
		else
			return '켜짐';
	}
	else
		return '수신전용';
};

var nativeCallback = function(msg){
	if(msg == 'back'){
		Native.loadUrl("login.html");
	}
};

//푸쉬 메시지 받는 콜백 /*
var recvDeviceControlMessage = function(res){
	var json = JSON.parse(res);

	if(json.action == 'device_control'){
		var type = $('.device_item.' + json.subid).attr('data-type');
		$('.device_item.' + json.subid).attr('data-value', json.value);		
		$('.device_item.' + json.subid + ' #type').removeClass();
		$('.device_item.' + json.subid + ' #type').addClass('b_t_b_horizontal_item');
		$('.device_item.' + json.subid + ' #type').addClass('float_left');
		$('.device_item.' + json.subid + ' #type').addClass(getIcon(type, json.value));
		$('.device_item.' + json.subid + ' #state').html(getValue(type, json.value));
	}
	else if(json.action == 'device_add'){
		var device = {subid:json.subid, type:json.type, name:json.name, value:json.value};
		$('#device_list').append(addDevice(device));		
		$('.no_item').addClass('hidden_option');
		deviceCnt++;
	}
	else if(json.action == 'device_del'){
		$('.device_item.' + json.subid).remove();
		deviceCnt--;
		if(deviceCnt == 0)
			$('.no_item').removeClass('hidden_option');
	}
}

init();

//자동로그인
//리스트 퍼포먼스