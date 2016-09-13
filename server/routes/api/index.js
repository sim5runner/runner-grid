/**
 * Created by AbhishekK
 */

'use strict';

// Get the router
var apirouter = require('express').Router();
var userData = require('../../controllers/userdata.server.controller.js');

// Middleware for all this apirouters requests
apirouter.use(function timeLog(req, res, next) {
  console.log('Request Received: ', dateDisplayed(Date.now()));
  next();
});

// Welcome message for a GET at http://localhost:8080/restapi
apirouter.get('/', function(req, res) {
    var rand = Math.random() * (9999999 - 9999) + 9999;
    res.writeHead(301,
        {Location: 'https://apiui.herokuapp.com?https://raw.githubusercontent.com/sim5runner/runner-v2/master/server/routes/api/docs/swagger.json&' + rand }
    );
    res.end();
});

// add data: error on existing data key for user
apirouter.post('/data', userData.addData);

// get all data for a user
apirouter.get('/data', userData.getData);

// get data for user
apirouter.get('/data/:userid', userData.getUserData);

// update data
apirouter.put('/data/:data_key', userData.updateData);

// update data: update data for user
apirouter.put('/data/:userid/:data_key', userData.updateUserData);

module.exports = apirouter;

function dateDisplayed(timestamp) {
    var date = new Date(timestamp);
    return (date.getMonth() + 1 + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
}