/**
 * Created by AbhishekK
 */

'use strict';

// Get the router
var apirouter = require('express').Router();
var stat = require('../../controllers/stat.server.controller');

// Middleware for all this apirouters requests
/*apirouter.use(function timeLog(req, res, next) {
  console.log('Request Received: ', dateDisplayed(Date.now()));
  next();
});*/

// add data: error on existing data key for user
apirouter.get('/server', stat.serverUsage);
apirouter.get('/tests', stat.runningTests);
apirouter.get('/tests/:clientip', stat.runningTest);

module.exports = apirouter;

function dateDisplayed(timestamp) {
    var date = new Date(timestamp);
    return (date.getMonth() + 1 + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
}