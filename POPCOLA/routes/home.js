const express = require('express');
const app = express.Router();
var data = require('../data.js'),
    request = require('request'),
    fs = require('fs'),
    omdb = require('omdb');
var Guid = require('Guid');
var xss = require("xss");

app.get("/", function (request, response) {

    response.render('pages/home.handlebars', {});
});

app.get("/nearByTheatres", function (request, response) {

    response.render('pages/nearByTheatre.handlebars', {});
});
app.post("/profile", function (request, response) {
    options = {
        whiteList: [],
        stripIgnoreTag: true,
        stripIgnoreTagBody: ['script']
    };
    myxss = new xss.FilterXSS(options);

    var fname = myxss.process(request.body.firstname);
    var lname = myxss.process(request.body.lastname);
    var email = myxss.process(request.body.email);
    var birth = myxss.process(request.body.birthyear);
    var currentSid = request.cookies.sessionId;

    data.updateProfile(currentSid, fname, lname, email, birth).then(function (user) {
        data.getLikeMovieByUser(user).then(function (movieList) {

            response.render('pages/profile', { movieList: movieList, data: user.profile, username: user.username, sid: user.currentSessionId, successInfo: null, updateError: null });
        });
    }, function (errorMessage) {
        data.getLikeMovieByUser(user).then(function (movieList) {
            response.status(500).render('pages/profile', { movieList: movieList, data: user.profile, username: user.username, sid: user.currentSessionId, successInfo: null, updateError: errorMessage });
        });
    });
});

app.get("/updateProfile", function (request, response) {
    if (request.cookies.sessionId) {
        var currentSid = request.cookies.sessionId;
        data.getUserBySessionId(currentSid).then(function (user) {

            data.getLikeMovieByUser(user).then(function (movieList) {

                response.render('pages/profile', { movieList: movieList, data: user.profile, username: user.username, sid: user.currentSessionId, successInfo: null, update: true, updateError: null });
            });

        }, function (errorMessage) {
            response.clearCookie("sessionId");
            response.status(500).render('pages/home', {});
        });
    } else {
        response.status(500).render('pages/home', { error: "YOU NEED TO LOGIN FIRST!" });

    }
})

app.get("/profile", function (request, response) {
    if (request.cookies.sessionId) {
        var currentSid = request.cookies.sessionId;
        data.getUserBySessionId(currentSid).then(function (user) {

            data.getLikeMovieByUser(user).then(function (movieList) {

                response.render('pages/profile', { movieList: movieList, data: user.profile, username: user.username, sid: user.currentSessionId, successInfo: null, updateError: null });
            });

        }, function (errorMessage) {
            response.clearCookie("sessionId");
            response.status(500).render('pages/home', {});
        });
    } else {
        response.status(500).render('pages/home', { error: "YOU NEED TO LOGIN FIRST!" });

    }
});

app.post("/login", function (request, response) {
    options = {
        whiteList: [],
        stripIgnoreTag: true,
        stripIgnoreTagBody: ['script']
    };
    myxss = new xss.FilterXSS(options);

    var uname = myxss.process(request.body.loginname);
    var pwd = myxss.process(request.body.loginpwd);
    var sid = Guid.create().toString();

    var expiresAt = new Date();
    expiresAt.setHours(expiresAt.getHours() + 1);

    data.loginUser(uname, pwd, sid).then(function (user) {
        response.cookie("sessionId", sid, { expires: expiresAt });
        data.getAllMovies().then(function (movieList) {
            response.render('pages/home', { movieList: movieList, data: user.profile, username: user.username, sid: user.currentSessionId, successInfo: null, updateError: null });
        });
    }, function (errorMessage) {
        response.status(500).render('pages/sign', { signupError: null, username: null, loginError: errorMessage });
    });
});

app.get("/login", function (request, response) {
    if (request.cookies.sessionId) {
        var currentSid = request.cookies.sessionId;
        data.getUserBySessionId(currentSid).then(function (user) {
            response.render('pages/sign.handlebars', { data: user.profile, username: user.username, sid: user.currentSessionId, successInfo: null, updateError: null, loginError: "You have already logged in!", signupError: null });
        }, function (errorMessage) {
            response.clearCookie("sessionId");
            data.getAllMovies().then(function (movieList) {
                response.status(500).render('pages/home', { movieList: movieList, signupError: null, username: null, loginError: "No cookies found, please log in again." });
            });
        });
    }
    else
        response.render('pages/sign', { signupError: null, username: null, loginError: null });
});


app.post("/signup", function (request, response) {
    options = {
        whiteList: [],
        stripIgnoreTag: true,
        stripIgnoreTagBody: ['script']
    };
    myxss = new xss.FilterXSS(options);

    var uname = myxss.process(request.body.signupname);
    var pwd = myxss.process(request.body.signuppwd);
    var cpwd = myxss.process(request.body.confirmpwd);
    var sid = Guid.create().toString();

    var expiresAt = new Date();
    expiresAt.setHours(expiresAt.getHours() + 1);
    response.clearCookie("sessionId");
    response.cookie("sessionId", sid, { expires: expiresAt });

    data.createUser(uname, pwd, cpwd, sid).then(function (user) {

        data.getLikeMovieByUser(user).then(function (movieList) {
            response.render('pages/profile', { movieList: movieList, data: user.profile, username: user.username, sid: sid, successInfo: null, updateError: null });
        });
    }, function (errorMessage) {
        response.status(500).render('pages/sign', { signupError: errorMessage, username: null, loginError: null });
    });
});

app.get("/signup", function (request, response) {
    response.render('pages/sign', { signupError: null, username: null, loginError: null });
});

app.get("/logout", function (request, response) {
    if (request.cookies.sessionId) {
        response.clearCookie("sessionId");
        data.getAllMovies().then(function (movieList) {
            response.render("pages/home", { movieList: movieList, signupError: null, username: null, loginError: "LOG OUT SUCCESSFULLY, LOG IN AGAIN" });
        });
    } else {
        data.getAllMovies().then(function (movieList) {
            response.render("pages/home", { movieList: movieList, signupError: null, username: null, loginError: "YOU ARE NOT LOGGED IN!!" });
        });
    }
});


app.post("/search", function (req, response) {
    var keyWord = req.body.keyword;
    return omdb.search(keyWord.toString(), function (err, movies) {
        if (err) {
            response.send("error");
        }
        else if (movies.length < 1) {
            response.send("No movies were found!");

        }
        else

            response.render('pages/search', { movieList: movies });
    })
})

app.get("/movie/:id", function (req, res) {
    var movie = req.params.id;

    var url = "http://www.omdbapi.com/?i=" + movie;
    request(url, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var data = JSON.parse(body);
        //    console.log(data)
            res.render("pages/movie.handlebars", { movieList: data });
        }
    });
});

app.post("/updateRating", function (request, response) {
    console.log(request.body)
    if (request.cookies.sessionId) {
        var currentSid = request.cookies.sessionId;
        var ratingIMDB = request.body.IMDB;
        var ratingComment = request.body.rating;

        data.getUserBySessionId(currentSid).then(function (user) {
            imdbID = user.profile.comments;

            for (var i in imdbID) {
                if (imdbID[i].IMDB === ratingIMDB) {
                
                    data.deleteRatings(currentSid, ratingIMDB).then(function (user) {
                       
                    })
                }
            }


            data.addRatings(currentSid, ratingIMDB, ratingComment).then(function (user) {
                response.redirect('/profile');

            })
        })



    }
    else{
        response.redirect('/')
    }
});



module.exports = app;