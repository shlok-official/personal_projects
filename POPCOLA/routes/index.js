const homePage = require("./home");

const constructorMethod = (app) => {
    app.use("/", homePage);

    app.use("*", (req, res) => {
        res.send("Wrong URL!").status(404);
    })
};

module.exports = constructorMethod;