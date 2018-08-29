var mongoose = require('mongoose');
var dataBase;

var Users;
var usersSchema = mongoose.Schema({
    name:String,
    id:String,
    password:String,
    guuid:String,
    type:String
});

var Devices;
var devicesSchema = mongoose.Schema({
	guuid:String,
	subid:String,
    name:String,
    type:String,
    value:String,
    gpio:String
});

var initDataBase = function(){
    dataBase = mongoose.connection;
    dataBase.on('error', console.error);
    dataBase.once('open', function(){
        console.log("Connected to mongoDB server");
    });

    mongoose.connect('mongodb://localhost/iothome');

    createSchema();
};

var createSchema = function(){
	usersSchema.methods.print = function(){
        console.log("user data - id : " + this.id + ", name : " + this.name + " insert ok!");
    };

    Users = mongoose.model('users', usersSchema);
    Devices = mongoose.model('devices', devicesSchema);
};

exports.insertUser = function(params, callback){
	var users = new Users(params);
	users.save(function(err, user){
        if(err)
            callback(-1, err);
        callback(0, '');
    });
};

exports.getUserList = function(conditions, callback){
	Users.find(conditions, function(err, items){
        if(err)
            callback(-1, err);
        callback(0, items);
    });
}

exports.getUserFindAll = function(callback){
	Users.find(function(err, items){
        if(err)
            callback(-1, err);
        callback(0, items);
    });
}

//devices
exports.addDevice = function(params, callback){
	var device = new Devices(params);
	device.save(function(err, user){
        if(err)
            callback(-1, err);
        callback(0, '');
    });
};

exports.delDevice = function(conditions, callback){
    Devices.remove(conditions, function(err, user){
        if(err)
            callback(-1, err);
        callback(0, '');
    });
};


exports.getDevices = function(conditions, callback){
	Devices.find(conditions, function(err, items){
        if(err)
            callback(-1, err);
        callback(0, items);
    });
}

exports.setDeviceValue = function(conditions, value, callback){
	Devices.update(conditions, value, function(err, items){
        if(err)
            callback(-1, err);
        callback(0, items);
    });
}

exports.start = function(){
	initDataBase();
};
