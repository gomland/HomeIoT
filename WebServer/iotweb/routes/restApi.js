var db = require('../database/dbManager');
var fcm = require('../firebase/fcm');
var appkey = 'bfj3kj340ask3m6ldmajkll2350kdf';

var CODE_0 = {result:'success', code:0, message:'성공'};
var CODE_0_1 = {result:'fail', code:-1, message:'실패'};
var CODE_1 = {result:'fail', code:1, message:'필수파라메터부족'};
var CODE_2 = {result:'fail', code:2, message:'인증실패'};
var CODE_3 = {result:'fail', code:3, message:'중복된이름'};
var CODE_4 = {result:'fail', code:4, message:'존재하지 않음'};
var CODE_5 = {result:'fail', code:5, message:'장치가 존재하지 않음'};
var CODE_6 = {result:'fail', code:6, message:'토큰등록실패'};
var CODE_7 = {result:'fail', code:7, message:'예외'};

exports.account = function(req, res){
	var key = req.get('key');

	if(appkey === key){
		var name = req.body.name;
		var id = req.body.id;
		var passwd = req.body.passwd;
		var guuid = req.body.guuid;
		var type = req.body.type;

		if(!name || !id || !passwd || !guuid || !type)
			res.json(CODE_1);	
		else{
			db.getUserList({'id':id}, function(code, msg){
				if(code == 0 && Object.keys(msg).length != 0)
					res.json(CODE_3);	
				else{
					var conditions = {'name':name, 'id':id, 'password':passwd, 'guuid':guuid, 'type':type};
					db.insertUser(conditions, function(code, msg){
						if(code == -1)
							res.json(CODE_0_1);	
						else
							res.json(CODE_0);				
					});
				}
			});			
		}
	}
	else
		res.json(CODE_2);	
};

exports.userInfo = function(req, res){
	var id = req.query.id;
	var passwd = req.query.passwd;

	if(!id || !passwd)
		res.json(CODE_1);
	else{
		var conditions = {'id' :id, 'password' : passwd};
		db.getUserList(conditions, function(code, msg){
			if(code == -1)
				res.json(CODE_0_1);	
			else if(code == 0 && Object.keys(msg).length == 0)
				res.json(CODE_4);	
			else
				res.json(CODE_0);	
		});		
	}
};


exports.userList = function(req, res){
	var key = req.get('key');

	if(appkey === key){
		db.getUserFindAll(function(code, msg){
			if(code == -1)
				res.json(CODE_0_1);
			else{
				var result = [];

				for(var i=0; i<msg.length; i++)
					result.push(msg[i].name);
				
				res.json(CODE_0);		
			}
		});		
	}
	else
		rres.json(CODE_2);
}


exports.addDevice = function(req, res){
	var key = req.get('key');

	if(appkey === key){		
		var guuid = req.body.guuid;
		var subid = req.body.subid;
		var name = req.body.name;
		var type = req.body.type;
		var value = req.body.value;
		var gpio = req.body.gpio;

		if(!name || !guuid || !type || !value || !subid || !gpio)
			res.json(CODE_1);	
		else{
			var conditions = {'guuid' : guuid, 'subid' : subid};
			db.getDevices(conditions, function(code, msg){		
				if(code == 0 && Object.keys(msg).length != 0)
					res.json(CODE_3);
				else{
					conditions = {'guuid':guuid, 'subid':subid, 'name':name, 'type':type, 'value':value, 'gpio':gpio};
					db.addDevice(conditions, function(code, msg){
						if(code == -1)
							res.json(CODE_0_1);
						else{
							var mobileMsg = {'action':'device_add', 'subid':subid, 'name':name, 'type':type, 'value':value};
							sendFcmTopic(guuid, 'MOBILE', mobileMsg);
							res.json(CODE_0);
						}
					});
				}
			});		
		}
	}
	else
		res.json(CODE_2);
};

exports.deleteDevice = function(req, res){
	var key = req.get('key');

	if(appkey === key){		
		var guuid = req.body.guuid;
		var subid = req.body.subid;

		if(!guuid || !subid)
			res.json(CODE_1);	
		else{
			var conditions = {'guuid' : guuid, 'subid' : subid};
			db.delDevice(conditions, function(code, msg){		
				if(code == 0){
					sendFcmTopic(guuid, 'MOBILE', {'action':'device_del', 'subid':subid});
					res.json(CODE_0);					
				}
				else
					res.json(CODE_0_1);
			});		
		}
	}
	else
		res.json(CODE_2);
};

exports.auther = function(req, res){
	var id = req.body.id;
	var passwd = req.body.passwd;
	var group = req.body.group;

	if(!id || !passwd || !group)
		res.json(CODE_1);
	else{
		var conditions = {'id' :id, 'password' : passwd};
		db.getUserList(conditions, function(code, msg){
			if(code == 0 && Object.keys(msg).length > 0){
				try{
					var guuid = msg[0].guuid;
					
					var params = {'group':group, 'guuid':guuid};
					db.getDevices({'guuid':guuid}, function(code, msg){		
						if(code == 0 && Object.keys(msg).length > 0)
							res.json({ result:'success', 'guuid':guuid, 'devices':msg});
						else
							res.json({ result:'success', 'guuid':guuid, 'devices':[]});
					});	
				}catch(e){
					res.json(CODE_4);
				}
			}
			else{
				res.json(CODE_4);
			}
		});		
	}
};

exports.controlDevice = function(req, res){
	var target = req.body.target;
	var guuid = req.body.guuid;
	var subid = req.body.subid;
	var value = req.body.value;

	if(!target || !guuid || !subid || !value)
		res.json(CODE_1);
	else{
		var conditions = {'guuid' :guuid, 'subid' : subid};
		db.setDeviceValue(conditions, {'value':value}, function(code, msg){
			if(code == 0){
				var message = {"action":"device_control", "subid":subid, "value":value};
				if(target == 'PAD')
					sendFcmTopic(guuid, 'MOBILE', message);
				sendFcmTopic(guuid, target, message);
				res.json(CODE_0);
			}
			else
				res.json(CODE_5);
		});
	}
};

exports.logout = function(req, res){
	
};

exports.groupSend = function(req, res){
	var action = req.body.action;
	var guuid = req.body.guuid;
	var target = req.body.target;

	if(!action || !guuid)
		res.json(CODE_1);
	else{
		var message = {"action":action};
		if(target == 'PAD')
			sendFcmTopic(guuid, 'MOBILE', message);
		sendFcmTopic(guuid, target, message);
	}
};

var sendFcmTopic = function(guuid, target, msg){
	db.getUserList({'guuid':guuid}, function(code, list){
		if(code == 0){
			if(list.length>0){
				var topics = "/topics/" + guuid + target;
				var message = {
				    to : topics,
				    collapse_key : "sshome", 
				    data: {"message" : msg},
				    notification: {
					        title: 'homeiot',
					        body: 'device control'
					}
				};
				console.log(message);
				fcm.send(message);
				console.log(topics + "그룹 메시지 발송.");			
			}
		}
		else
			console.log("메시지 발송 실패!");
	});	
};