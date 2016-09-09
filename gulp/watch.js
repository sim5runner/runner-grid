'use strict';

var gulp = require('gulp');

function isOnlyChange(event) {
    return event.type === 'changed';
}

module.exports = function(options) {
    gulp.task('watch', function () {
        gulp.watch([
            options.src + '/css/less/*.less'
        ], ['styles']
        );
    });
};
