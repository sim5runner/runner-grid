/**
 * Created by AbhishekK on 9/13/2016.
 */

'use strict';

module.exports = function(app, config) {
    var mongoose = require("mongoose");

    try{
        var mongoURL = config.mongo.prefix  + config.mongo.username + ":" + config.mongo.password + "@" + config.mongo.dbURL;
        mongoose.connect(mongoURL);

        var conn = mongoose.connection;
        conn.on('error', console.error.bind(console, 'Mongo connection error:'));

        conn.once('open', function() {
            console.log('Mongo Connection Successful');
        });
    } catch(er){
        console.log("Mongo error" + er);
    }
};