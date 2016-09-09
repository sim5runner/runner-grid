var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

// define the schema for user model
var userSchema   = new Schema({
    username: String,
    password: String,
    salt: String,
    profile: {
        name: String,
        email: String,
        selenium: {},
        svn_credentials: {}
    }
});

module.exports = mongoose.model('Users', userSchema);