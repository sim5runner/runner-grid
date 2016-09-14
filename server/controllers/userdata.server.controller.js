/**
 * Created by AbhishekK
 */
'use strict';

const router = require('express').Router();
var userData = require('../controllers/userdata.server.controller');

exports.addData = function (req, res) {
    res.json({});
};

exports.getData = function (req, res) {
    res.json({});
};

exports.getUserData = function (req, res) {
    res.json({"data_key": req.param.data_key,
        "data_value": "test value"
    });
};

exports.updateUserData = function (req, res) {
    res.json({});
};