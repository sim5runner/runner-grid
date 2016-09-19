'use strict';
//todo;fix for server to pic file from app in development mode
var gulp = require('gulp'),
    inject = require('gulp-inject'), //https://www.npmjs.com/package/gulp-inject
    path = require('path'),
    wrench = require('wrench');

var options = {
    src: 'app',
    server: 'server',
    dist: 'dist',
    tmp: '.tmp',
    paths : {
        less: ['./app/css/**/*.less',
            './app/css/*.css'
        ],

        js_dev: [],
        js_dev_lazy: [
            './app/js/*.js',
        ],

        css_dev: [
            './.tmp/css/*.css',
            './.tmp/css/**/*.css'
        ],
        css_dev_lazy: [],

        js_dist: [
            './dist/scripts/*.js'
        ],
        css_dist: [
            './dist/styles/*.css',
        ]
    },
    wiredep: {
        directory: 'bower_components',
        exclude: [
            /bootstrap-sass-official\/.*\.js/,
            /bootstrap\.css/,
            /open-sans-fontface\/.*/
        ],
        fileTypes: {
            html: {
                replace: {
                    js: function (filePath) {
                        var options = '';
                        if (filePath.match(/pace\.js/)) {
                            options = " data-pace-options='{ \"target\": \".content-wrap\", \"ghostTime\": 1000 }'"
                        }
                        return '<script' + options + ' src="' + filePath + '"></script>';
                    }
                }
            }
        },
        ignorePath: /^(\.\.\/)+/
    }
};

wrench.readdirSyncRecursive('./gulp').filter(function(file) {
    return (/\.(js|coffee)$/i).test(file);
}).map(function(file) {
    require('./gulp/' + file)(options);
});

gulp.task('default', ['clean'], function () {
    gulp.start('build');
});

