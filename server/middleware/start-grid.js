/**
 * Created by AbhishekK on 9/13/2016.
 */

'use strict';

var util = require('../utils');

module.exports = function(app, config) {

    util.portInUse(4444, function(returnValue) {
        if(returnValue === false) {

            //console.log('starting grid' + returnValue)
            var cmd = "java -jar selenium-server-standalone-2.41.0.jar -role hub";

            var process = require('child_process');
            var ls;

            //todo: change dir path
            var options = { cwd: _serverDirectory+"/server/lib",
                env: process.env
            };

            ls = process.spawn('cmd.exe', ['/c', cmd], options);

            ls.stdout.on('data', function(data){
                console.log(util.ab2str(data));
            })

            ls.stderr.on('data', function (data) {
                console.log(util.ab2str(data));
            });

            ls.on('exit', function (code) {
                console.log('grid server started with code ' + code);
            });

        }
    });

};