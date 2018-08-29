var webCallback;
var serverIp = "192.168.0.111";

var Native = {
	webLogin : function(id, passwd, callback){
		webCallback = callback;
		window.interface.webLogin(serverIp, id, passwd);
	},

	setPrefData : function(key, data){
		window.interface.setPrefs(key, data);
	},

	getPrefData : function(key, callback){
		webCallback = callback;
		window.interface.getPrefs(key);
	},

	changedNativeBottom : function(msg){
		window.interface.changedNativeBottom(msg);
	},

	deviceControl : function(guuid, subid, type, value){
		window.interface.deviceControl(guuid, subid, type, value);
	},

	loadUrl : function(url){
		window.interface.loadUrl(url);	
	},

	startProgress : function(msg){
		window.interface.startProgress(msg);		
	},

	startResultPopup : function(msg){
		window.interface.startResultPopup(msg);		
	},

	popupStop : function(){
		window.interface.popupStop();		
	}
};

Native.setPrefData('key_server_ip', serverIp);