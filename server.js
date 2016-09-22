/**
 * Created by AbhishekK
 */

'use strict';

/**
 * dependencies
 */
const express = require('express');
const exphbs = require('express-handlebars');
const expressValidator = require('express-validator');
const compress = require('compression');
const bodyParser = require('body-parser');
const path = require('path');

/**
 * @type {Logger|exports}
 */
const bunyan = require("bunyan");
const bunyanformat = require('bunyan-format');
const formatOut = bunyanformat({ outputMode: 'short' });
const logger = bunyan.createLogger({name: 'RunnerGrid', stream: formatOut, level: 'info' });

/**
 * @type {App|exports}
 */
const config = require("./server/config");
let app = express();
GLOBAL._serverDirectory = __dirname;

/**
 * Middlewares
 */
// 1. HTTP CACHE HEADERS
app.use(function(req, res, next) {

    if (req.url.match(/^\/(css|js)\/.+/)) {
        // set cacahe time in header as 3 days.
        res.setHeader('Cache-Control', 'public, max-age=259200');
        // while calculating the expiry date, add 3 days(change to milliseconds), in the current date.
        res.setHeader("Expires", new Date(Date.now() + 259200000).toUTCString());
    }
    return next();
});

// 2. GZIP compression
app.use(compress());

//Default location of Express Views - used in development mode
let viewsPath = path.join(__dirname, '.tmp', 'views');

//Environment setup production / development
if (process.env.NODE_ENV === 'production') {
    // Override Views location to dist folder
    viewsPath = path.join(__dirname, 'dist');
    app.use(express.static(__dirname + '/dist/'));
    app.set('views', viewsPath);
} else {
    // make express look in the public directory for assets (css/js/img)
    app.use(express.static(__dirname + '/app/'));
    app.use(express.static(__dirname + '/.tmp/'));
    app.set('views', viewsPath);
}

// 3.Support for  json encoded bodies
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(expressValidator());

/**
 * Express WWW Server
 */
let port = process.env.PORT || 9001;
// Serve Bower Components based JS & CSS & Image assets
app.use("/bower_components", express.static(__dirname + '/bower_components'));

// View engine setup (handlebars) based in viewsPath defined earlier
app.engine('.hbs', exphbs({
    defaultLayout: 'default',
    layoutsDir: viewsPath + '/layouts',
    partialsDir: viewsPath + '/partials',
    extname: '.hbs'
}));
app.set('view engine', '.hbs');

/**
 * Middleware imports
 */
require("./server/routes")(app, config);
require("./server/middleware/mongoose")(app, config);

require("./server/middleware/start-grid")(app, config);
require("./server/middleware/clone-jf")(app, config);

/**
 * Error handling
 */
app.use(function(err, req, res, next){
    logger.error(err);
    res.status(err.status)
        .send(err);
});

/**
 * Socket Conn
 */

var http = require('http').Server(app);
GLOBAL._io = require('socket.io')(http);

/**
 * Start listening
 */

http.listen(port, function() {
    logger.info('Your Automation App is running on http://localhost:' + port);
    logger.info('Environment is set to ' + (process.env.NODE_ENV || 'development'));
});
