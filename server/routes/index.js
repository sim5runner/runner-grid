/**
 * Created by AbhishekK
 */

'use strict';
module.exports = function(app) {
    app.use('/', require('./web.server.route.js'));
    app.use('/api', require('./api'));
    app.use('/sims', require('./sims'));
};