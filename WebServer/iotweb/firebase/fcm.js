var FCM = require('fcm-push');

var serverKey = "aaaaaaa";
var fcm = new FCM(serverKey);

exports.send = function(message){
	fcm.send(message, function(err, response){
	    if (err) {
	        console.log("Something has gone wrong!");
	    } else {
	        console.log("Successfully sent with response: ", response);
	    }
	});
}