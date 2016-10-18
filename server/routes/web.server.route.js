'use strict';
var express = require('express');

// Get the router
var webrouter = express.Router();

webrouter.get('/', function(req, res) {
    res.setHeader('Cache-control', ['no-cache','no-store','must-revalidate']);
    res.setHeader('Pragma', 'no-cache');
    res.render('index',{ username: 'grid-server', name: 'grid-server' });
});

webrouter.get('/stat', function(req, res) {
    res.setHeader('Cache-control', ['no-cache','no-store','must-revalidate']);
    res.setHeader('Pragma', 'no-cache');
    res.render('stat',{ username: 'grid-server', name: 'grid-server' });
});

webrouter.get('/svn', function(req, res) {
    res.setHeader('Cache-control', ['no-cache','no-store','must-revalidate']);
    res.setHeader('Pragma', 'no-cache');
    res.render('svn',{ username: 'grid-server', name: 'grid-server' });
});


module.exports = webrouter;
