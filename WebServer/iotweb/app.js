var express = require('express');
var http = require('http');
var path = require('path');
var favicon = require('static-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var restApi = require('./routes/restApi');

var database = require('./database/dbManager');
var app;

var init = function(){
    initApp();
    database.start();

    console.log('service service...');
    module.exports = app;
};

var initApp = function(){
    app = express();

    // view engine setup
    app.set('views', path.join(__dirname, 'views'));
    app.set('view engine', 'jade');

    app.use(favicon());
    app.use(logger('dev'));
    app.use(bodyParser.json());
    app.use(bodyParser.urlencoded());
    app.use(function(req, res, next){
        var data='';
        req.on('data', function(chunk){
            data+=chunk;
        });
        req.on('end', function(){
            req.rawBody = data;
        });
        next();
    });
    app.use(cookieParser());
    app.use(express.static(path.join(__dirname, 'public')));
    app.use(app.router);

    /// catch 404 and forwarding to error handler
    app.use(function(req, res, next) {
        var err = new Error('Not Found');
        err.status = 404;
        next(err);
    });

    /// error handlers

    // development error handler
    // will print stacktrace
    if (app.get('env') === 'development') {
        app.use(function(err, req, res, next) {
            res.render('error', {
                message: err.message,
                error: err
            });
        });
    }

    // production error handler
    // no stacktraces leaked to user
    app.use(function(err, req, res, next) {
        res.render('error', {
            message: err.message,
            error: {}
        });
    });

    setRouting();
};

var setRouting = function(){
    app.post('/reg/account', restApi.account);
    app.get('/reg/userinfo', restApi.userInfo);
    app.get('/reg/userList', restApi.userList);
    app.post('/auther', restApi.auther);
    app.post('/device/add', restApi.addDevice);
    app.patch('/device/set', restApi.controlDevice);
    app.post('/device/del', restApi.deleteDevice);
    app.post('/group/send', restApi.groupSend);
    app.post('/logout', restApi.logout);    
};

init();
