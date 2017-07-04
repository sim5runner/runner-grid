/**
 * Created by AbhishekK
 */
'use strict';

var util = require('../../utils');
var fs = require('fs');
var mkdirp = require('mkdirp');

exports.pushResults = function (req, res) {
    console.log(req.body);
    // get request from req body
    // translate results
    // post results to builder

    res.json(
        {
            status:"success",
            message:"request received"
        }
    );
};
