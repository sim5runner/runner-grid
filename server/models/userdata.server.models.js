/**
 * Created by AbhishekK
 */

'use strict';

var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

// define the schema for xpath model
var userData   = new Schema({
    key: String,
    value: String,
    tags: String,
    user: {
        name: String,
        email: String
    }
});

module.exports = mongoose.model('xpath', userData);