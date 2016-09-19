/**
 * Created by AbhishekK
 */
'use strict';
const os = require('os');

exports.serverUsage = function (req, res) {
    res.json({
        memory:{
            free:  (os.freemem() / (1024*1024)),
            total: (os.totalmem() / (1024*1024)),
            unit: "MB"
        }
    });
};
