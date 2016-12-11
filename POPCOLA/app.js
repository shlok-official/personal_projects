// We first require our express package
var express = require('express');
var bodyParser = require('body-parser');
var data = require('./data.js');
var cookieParser = require('cookie-parser');
var Guid = require('Guid');
var fs = require('fs');
var xss = require("xss");

const configRoutes = require("./routes");
const exphbs = require('express-handlebars');

const Handlebars = require('handlebars');

const handlebarsInstance = exphbs.create({
    defaultLayout: 'main',
    // Specify helpers which are only registered on this instance.
    helpers: {
        asJSON: (obj, spacing) => {
            if (typeof spacing === "number")
                return new Handlebars.SafeString(JSON.stringify(obj, null, spacing));

            return new Handlebars.SafeString(JSON.stringify(obj));
        }
    }
});

const rewriteUnsupportedBrowserMethods = (req, res, next) => {
    // If the user posts to the server with a property called _method, rewrite the request's method
    // To be that method; so if they post _method=PUT you can now allow browsers to POST to a route that gets
    // rewritten in this middleware to a PUT route
    if (req.body && req.body._method) {
        req.method = req.body._method;
        delete req.body._method;
    }

    // let the next middleware run:
    next();
};

// This package exports the function to create an express instance:
var app = express();

app.engine('handlebars', handlebarsInstance.engine);
app.set('view engine', 'handlebars');

app.use(cookieParser());
app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.use('/assets', express.static('static'));
app.use(function(request, response, next) {
    var sessionId = request.cookies.sessionId;
    if (sessionId) {

        data.getUserBySessionId(sessionId).then(function(user) {
            response.locals.user = user;
        });
    } else {
        response.locals.user = { username: null, profile: null, currentSessionId: null };

    }
    next();
});

configRoutes(app);
// We can now navigate to localhost:3000
app.listen(3000, function() {
    console.log('Your server is now listening on port 3000! Navigate to http://localhost:3000 to access it');
});