var express = require("express");
var app = express();
app.set("view engine","ejs");
var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({extended: true}));
var request = require("request");


app.get("/",function(req,res){
    res.render("search");
});

app.get("/results",function(req,res){
    var movie = req.query.movieinput;
    
    var url ="http://www.omdbapi.com/?s=" + movie;
    request(url, function (error, response, body) {
        if (!error && response.statusCode == 200) {
             var data = JSON.parse(body);
                res.render("results",{data:data});
         }
    });
});




app.listen(process.env.PORT,process.env.IP,function(){
    console.log("Movie Database live");
});