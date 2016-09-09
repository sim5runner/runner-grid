'use strict';

// load all the things we need
var LocalStrategy   = require('passport-local').Strategy;
var crypto = require('crypto');
var Users     = require('./../models/app.server.models.user');

var hashPassword = function(password, saltValue) {
    return crypto.pbkdf2Sync(password, saltValue, 10000, 64).toString('base64');
};

module.exports = function(passport) {

    // passport session setup
    // required for persistent login sessions
    // passport needs ability to serialize and deserialize users out of session

    passport.serializeUser(function(user, done) {
        done(null, user.username);
    });

    passport.deserializeUser(function(username, done) {
        Users.findOne({username: username}, function(err, user) {
            done(err, user);
        });
    });

    passport.use('local',
        new LocalStrategy({
                passReqToCallback : true
            },
            function(req, username, password, done) {

                Users.findOne({username: username}, function(err, user) {

                    if (err || user === null) {return done({message:'User not found'}, false)}

                    if (username === null) {
                        console.log('Credentials not provided');
                        return done({message:'Credentials not provided'}, false);
                    }

                    if(user.username !== username){
                        console.log('User Not Found with username '+username);
                        return done({message:'User Not Found with username' + username}, false);
                    }

                    if(user.username === username && user.password !== password){
                        console.log('Incorrect password');
                        return done({message:'Incorrect password'}, false);
                    }

                    if(user.username === username && user.password === password){
                        console.log('Successfully authenticated ' + username);
                        return done(null, user);
                    }
                });

            }
    ));
};