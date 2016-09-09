'use strict';

var gulp = require('gulp');
var jshint = require('gulp-jshint');
var $ = require('gulp-load-plugins')({
    pattern: ['gulp-*', 'main-bower-files', 'uglify-save-license', 'del']
});

module.exports = function(options) {
  gulp.task('dev-scripts', function () {

      return gulp.src(options.src + '/js/*.js')
          .pipe(gulp.dest(options.tmp + '/js'))
	      .pipe($.concat('app.js'))
          .pipe(gulp.dest(options.tmp + '/serve/scripts'));

	//return gulp.src(options.src + '/**/*.js')
    //      .pipe(jshint())
    //.pipe(jshint.reporter('jshint-stylish'))

  });


    gulp.task('scripts',['dev-scripts'], function () {

        return gulp.src(options.tmp + '/serve/**/*.*')
            .pipe($.rev())
            .pipe(gulp.dest(options.dist + '/'));
           
    });

};
