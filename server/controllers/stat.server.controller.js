/**
 * Created by AbhishekK
 */
'use strict';
const os = require('os');
var util = require('../utils')
exports.serverUsage = function (req, res) {
    res.json({
        memory:{
            free:  (os.freemem() / (1024*1024)),
            total: (os.totalmem() / (1024*1024)),
            unit: "MB"
        }
    });
};

exports.runningTests = function (req, res) {
    res.json({
        total: _runningTests.length,
        tests:_runningTests
    });
};

exports.runningTest = function (req, res) {

    var ip = req.param.clientip;

    var ret = util.searchNodeInArray(_runningTests,{ip:ip});
    res.json({
        test:ret
    });
};




