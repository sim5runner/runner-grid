/**
 * Created by AbhishekK on 9/13/2016.
 */

'use strict';

var util = require('../utils');

module.exports = function(app, config) {

    var cmd = "java -jar selenium-server-standalone-2.41.0.jar -role hub";

    var process = require('child_process');
    var ls;

    //todo: change dir path
    var options = { cwd: "H:/runner-grid/server/lib",
        env: process.env
    };

    ls = process.spawn('cmd.exe', ['/c', cmd], options);

    ls.stdout.on('data', function(data){
        //console.log('tt')
        //io.emit('stream', {n:ab2str(data)});
        //io.sockets.on('connection', function (socket) {
        //io.sockets.broadcast.emit('stream', {n:data});
        //});

        console.log(util.ab2str(data));
    })

    ls.stderr.on('data', function (data) {
        //io.emit('stream', {n:ab2str(data)});

        console.log(util.ab2str(data));
    });

    ls.on('exit', function (code) {
        //io.emit('stream', {n:ab2str(code)});

        console.log('child process exited with code ' + code);
    });

};