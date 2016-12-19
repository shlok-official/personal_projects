var express         = require("express"),
     app             = express(),
     bodyParser      = require("body-parser"),
     request         = require("request"),
     mongoose        = require("mongoose"),   
     expressSanitizer= require("express-sanitizer"),
     methodOverride  = require("method-override");
mongoose.connect("mongodb://localhost/restful_blog");
app.set("view engine","ejs");
app.use(bodyParser.urlencoded({extended: true}));
app.use(express.static("public"));
app.use(methodOverride("_method"));
app.use(expressSanitizer());
//MONGOOSE SCHEMA CONFIG
var blogSchema = new mongoose.Schema({
    title:String,
    image:String,
    body:String,
    created:{
            type: Date, 
            default: Date.now
             }
});
var Blog = mongoose.model("Blog",blogSchema);

// Blog.create({
//     title:"Shlok Daily Dairy",
//     image:"http://198.74.57.119/wordpress/wp-content/uploads/2014/01/shlokfb23.jpg",
//     body:"Initial post to keep you posted"
// });

//RESTFUL ROUTES

app.get("/",function(req, res) { 
    res.redirect("/blogs");
})
//INDEX route
app.get("/blogs",function(req,res){
    Blog.find({},function(err,blogs){
       if(err){
           console.log(err)
       } else{
           res.render("index",{blogShow:blogs});  
       }
    });
});
//NEW ROUTE
app.get("/blogs/new",function(req, res) {
    res.render("new");
});

//CREATE ROUTE
app.post("/blogs",function(req,res){
    //creatre and redirect
    Blog.create(req.body.blog,function(err,newBlog){
    if(err){
        res.render("new");
    }else{
        res.redirect("/blogs");
        }
    });
});

//SHOW ROUTE
app.get("/blogs/:id",function(req, res) {
    // res.send("Show Page");
     Blog.findById(req.params.id,function(err,foundBlog){
         if(err){
        res.redirect("/blogs");
         }else{
             res.render("shows",{blog:foundBlog});
         }
     });
});

//EDIT ROUTE
app.get("/blogs/:id/edit",function(req, res) {
    Blog.findById(req.params.id,function(err,foundBlog){
         if(err){
        res.redirect("/blogs");
         }else{
            res.render("edit",{blog:foundBlog}); 
         }
    });
});

//UPDATE REQUEST
app.put("/blogs/:id",function(req,res){
    //res.send("UPDATE ROUTE")
    Blog.findByIdAndUpdate(req.params.id,req.body.blog,function(err,updatedBlog){
        if(err){
            res.redirect("/blogs");
        }else{
            res.redirect("/blogs/"+req.params.id);
        }
    });
});
 
//DELETE ROUTE
app.delete("/blogs/:id",function(req,res){
   Blog.findByIdAndRemove(req.params.id,function(err){
       if(err){
           res.redirect("/blogs");
       }else{
           res.redirect("/blogs");
       }
   });
});


//SERVER LISTENING
app.listen(process.env.PORT,process.env.IP,function(){
    console.log("SERVER IS ON");
});


//image
//title
//body
//date created