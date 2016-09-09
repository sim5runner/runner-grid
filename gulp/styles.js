'use strict';

var gulp = require('gulp');
var less = require('gulp-less');
var path = require('path');
var $ = require('gulp-load-plugins')();

module.exports = function(options) {
    gulp.task('dev-styles', function () {
        return gulp.src([
            options.src + '/css/less/custom.less'
        ])
        .pipe(less({
            paths: [ path.join(__dirname, 'less', 'includes') ]
        }))
        .pipe(gulp.dest(options.tmp + '/css'))
		.pipe($.concat('app.css'))
        .pipe(gulp.dest(options.tmp + '/serve/styles'));
    });

    gulp.task('styles',['dev-styles'], function () {

    	return gulp.src(options.tmp + '/serve/**/*.*')
            .pipe($.rev())
            .pipe(gulp.dest(options.dist + '/'));
            
    });
};
