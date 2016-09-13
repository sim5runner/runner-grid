'use strict';
var express = require('express');

// Get the router
var webrouter = express.Router();

webrouter.get('/', function(req, res) {
    res.setHeader('Cache-control', ['no-cache','no-store','must-revalidate']);
    res.setHeader('Pragma', 'no-cache');
    res.render('index',{ username: 'grid-server', name: 'grid-server' });
});

module.exports = webrouter;
