/**
 * Module dependencies.
 */

var http = require('http');
var express = require('express'),
    routes = require('./routes');

// Configuration
const PORT = 3000;
const AUDIENCE = "http://localhost:" + PORT;

var app = express();

app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(express.cookieParser());
  app.use(express.session({ secret: 'your secret here' }));
  app.use(express.csrf());
  app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
  app.locals.pretty = true;
});

app.configure('production', function(){
  app.use(express.errorHandler());
});

// Routes

app.get('/', routes.index);
app.post('/auth', routes.auth(AUDIENCE));
app.get('/logout', routes.logout);

var server = http.createServer(app);
server.listen(PORT, function() {
    console.log("Express server listening on port %d in %s mode", PORT, app.settings.env);
});

module.exports = server;
