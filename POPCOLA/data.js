var MongoClient = require('mongodb').MongoClient,
    settings = require('./config.js'),
    Guid = require('Guid'),
    bcrypt = require('bcrypt-nodejs'),
    request = require('request'),
    fs = require('fs'),
    rp = require('request-promise'),
    omdb = require('omdb');
var movieCollection, userCollection;

var fullMongoUrl = settings.mongoConfig.serverUrl + settings.mongoConfig.database;
var exports = module.exports = {};

MongoClient.connect(fullMongoUrl)
    .then(function (db) {
        db.createCollection("users");
        db.createCollection("movies");
        userCollection = db.collection("users");
        movieCollection = db.collection("movies");
    });

exports.createUser = function (uname, pwd, cpwd, sid) {
    if (!uname) return Promise.reject("You must provide a username");
    if (!pwd) return Promise.reject("You must provide a password");
    if (!cpwd) return Promise.reject("You must provide a confirm password");
    if (!sid) return Promise.reject("You must provide a sessionId");

    if (pwd != cpwd) {
        return Promise.reject("The confirmed password does not match. Please try again.");
    }

    var hashpwd = bcrypt.hashSync(pwd);

    return userCollection.find({ username: uname }).limit(1).toArray().then(function (listOfUsers) {
        if (listOfUsers.length != 0) {
            throw "This username is already in use.";
        }
        return userCollection.insertOne({
            _id: Guid.create().toString(),
            username: uname,
            encryptedPassword: hashpwd,
            currentSessionId: sid,
            like: [],
            profile: {
                fname: "",
                lname: "",
                email: "",
                birthYear: 0,
                comments: []
            }

        }).then(function (newDoc) {
            return exports.getUserById(newDoc.insertedId);
        });
    });
};

exports.loginUser = function (uname, pwd, sid) {
    if (!uname) return Promise.reject("You must provide a username");
    if (!pwd) return Promise.reject("You must provide a password");
    if (!sid) return Promise.reject("You must provide a sessionId");

    return userCollection.find({ username: uname }).limit(1).toArray().then(function (listOfUsers) {
        if (listOfUsers.length === 0) {
            throw "Could not find user with name of " + uname;
        }
        if (!bcrypt.compareSync(pwd, listOfUsers[0].encryptedPassword)) {
            throw "We are unable to match your username and password. ";
        }
        exports.updateSid(uname, sid);
        return listOfUsers[0];
    });
};

exports.updateProfile = function (sid, fname, lname, email, birthYear) {
    return userCollection.update({ currentSessionId: sid }, {
        $set: {
            "profile.fname": fname,
            "profile.lname": lname,
            "profile.email": email,
            "profile.birthYear": birthYear
        }
    }).then(function () {
        return exports.getUserBySessionId(sid);
    });
};

exports.likeBySid = function (sid, imdbid) {
    return userCollection.find({ currentSessionId: sid }).limit(1).toArray().then(function (listOfUsers) {
        if (listOfUsers.length === 0) {
            throw "Could not find user with sid of " + sid;;
        }
        var array = listOfUsers[0].like;
        var index = array.indexOf(imdbid);

        if (index > -1)
            array.splice(index, 1);
        else
            array.push(imdbid);

        return userCollection.update({ currentSessionId: sid }, {
            $set: {
                like: array

            }
        }).then(function () {
            return exports.getUserBySessionId(sid);
        });
    });
};

exports.updateSid = function (uname, sid) {
    if (!uname) return Promise.reject("You must provide a username");
    if (!sid) return Promise.reject("You must provide a sid");

    return userCollection.update({ username: uname }, { $set: { currentSessionId: sid } }).then(function () {
        return exports.getUserBySessionId(sid);
    });
};

exports.getUserByName = function (uname) {
    if (!uname) return Promise.reject("You must provide a name");

    return userCollection.find({ username: uname }).limit(1).toArray().then(function (listOfUsers) {
        if (listOfUsers.length === 0) {
            throw "Could not find user with name of " + uname;
        }
        return listOfUsers[0];
    });
};

exports.getUserById = function (id) {
    if (!id) return Promise.reject("You must provide an ID");

    return userCollection.find({ _id: id }).limit(1).toArray().then(function (listOfUsers) {
        if (listOfUsers.length === 0) {
            throw "Could not find user with id of " + id;
        }
        return listOfUsers[0];
    });
};

exports.getUserBySessionId = function (sid) {
    if (!sid) return Promise.reject("You must provide an SID");

    return userCollection.find({ currentSessionId: sid }).limit(1).toArray().then(function (listOfUsers) {
        if (listOfUsers.length === 0) {
            throw "Could not find user with sid of " + sid;;
        }
        return listOfUsers[0];
    });
};

exports.getAllUsers = function () {
    return userCollection.find().toArray();
}


exports.getAllMovies = function () {
    return movieCollection.find().toArray();
}

exports.getLikeMovieByUser = function (user) {
    likeList = user.like;
    return movieCollection.find({ imdbId: { $in: likeList } }).toArray();
}

exports.addRatings = function (sid, ratingIMDB, ratingComment) {
    var commentData = {
        "IMDB": ratingIMDB,
        "Rating": ratingComment
    }
   //  console.log(sid, commentData)
    return userCollection.update({ currentSessionId: sid }, {
        $addToSet: {
            "profile.comments": commentData
        }
    }).then(function () {
       // console.log("saved")
        return exports.getUserBySessionId(sid);
    });
};

exports.deleteRatings = function (sid, ratingIMDB) {
 
    return userCollection.update({ currentSessionId: sid }, {
        $pull: {
            "profile.comments":{"IMDB": ratingIMDB}
        }
    }).then(function () {
       // console.log("saved")
        return exports.getUserBySessionId(sid);
    });
};