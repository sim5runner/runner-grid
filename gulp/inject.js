'use strict';

var gulp = require('gulp');

var $ = require('gulp-load-plugins')();
var wiredep = require('wiredep').stream;

module.exports = function(options) {

  gulp.task('inject-dev', ['styles', 'scripts'], function () {
    var injectStyles = gulp.src(options.paths.css_dev,
        {read: false});

    var injectScripts = gulp.src(options.paths.js_dev,
        {read: false});

    var injectScriptsLazy =  gulp.src(options.paths.js_dev_lazy,
        {read: false});

    var injectStylesLazy =  gulp.src(options.paths.css_dev_lazy,
        {read: false});

    var injectOptions = {relative: false,ignorePath: ['app','.tmp'], addRootSlash: false};

    return gulp.src(options.server + '/views/**/*.hbs')
      .pipe($.inject(injectStyles, injectOptions))
      .pipe($.inject(injectScripts, injectOptions))
      .pipe($.inject(injectScriptsLazy,{name: 'lazy',relative: false,ignorePath: ['app','.tmp'], addRootSlash: false}))
      .pipe($.inject(injectStylesLazy,{name: 'lazy',relative: false,ignorePath: ['app','.tmp'], addRootSlash: false}))
      .pipe(wiredep(options.wiredep))
      .pipe(gulp.dest(options.tmp + '/views'))

  });

  gulp.task('inject',['inject-dev'], function () {

      var injectStyles = gulp.src(options.paths.css_dist,
          {read: false});

      var injectScripts = gulp.src(options.paths.js_dist,
          {read: false});

      var injectOptions = {relative: false,ignorePath: ['app','.tmp','serve', 'dist'], addRootSlash: false};

      return gulp.src(options.server + '/views/**/*.hbs')
          .pipe($.inject(injectStyles, injectOptions))
          .pipe($.inject(injectScripts, injectOptions))
          .pipe(wiredep(options.wiredep))
          .pipe(gulp.dest(options.dist))

  });
};
