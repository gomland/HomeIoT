var isChecked;

var init = function(){
	Native.getPrefData('auto_login', function(checked){
		console.log('shin checked:' + checked);
		if(checked == 'Y'){
			isChecked = 'Y';
			changeChecked(true);			
			Native.getPrefData('login_id', function(id){
				var loginId = id;
				Native.getPrefData('login_pw', function(pw){
					console.log("shin " + loginId + ", " + pw);
					$('#id').val(loginId);
					$('#passwd').val(pw);
					login();
				});
			});
		}
	});

	$('#check').click(function(){
		if(isChecked == 'Y'){
			isChecked = 'N';
			changeChecked(false);
		}
		else{
			isChecked = 'Y';
			changeChecked(true);
		}
	});

	$('#login_btn').click(function(){
		login();
	});
};

var login = function(){
	var id = $('#id').val();
	var passwd = $('#passwd').val();

	if(id.length == 0)
		Native.startResultPopup('아이디를 입력해주세요.');
	else if(passwd.length == 0)
		Native.startResultPopup('패스워드를 입력해주세요.');
	else{
		Native.startProgress('인증 중...');
		Native.webLogin(id, passwd, function(msg){
			Native.popupStop();
			
			var json = JSON.parse(msg);
			if(json.result == "success"){
				Native.setPrefData('login_id', id);
				console.log("shin " + id + "," + passwd);
				Native.setPrefData('login_pw', passwd);
				if(isChecked == 'Y')
					Native.setPrefData('auto_login', 'Y');
				else
					Native.setPrefData('auto_login', 'N');
				Native.setPrefData('key_guuid', json.guuid);
				Native.setPrefData('key_devices', msg);				
				Native.changedNativeBottom("show_btn");
				Native.loadUrl("home.html");
			}
			else
				Native.startResultPopup('인증 실패');			
		});
	}
}

var changeChecked = function(bool){
	if(bool == true){
		$('#check').removeClass('check_bg');
		$('#check').addClass('check_bg_p');
	}	
	else{
		$('#check').addClass('check_bg');
		$('#check').removeClass('check_bg_p');
	}
}

$(document).ready(function(){
    init();
});
