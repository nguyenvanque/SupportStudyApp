const Courses = require("../models/Courses")
const {
  mutipleMongooseToObject,
  mongooseToObject,
} = require("../../util/mongoose")

class SitesController {
  //[GET], /
  index(req, res, next) {
    Courses.find({})
      .then((courses) => {
        //map qua de bien thang nay thanh object thuong con contructor no khong doc duoc
        res.render("home", {
          courses: mutipleMongooseToObject(courses),
        })
      })
      .catch(next)
  }

  //[GET],/:slug
  search(req, res) {
    res.render("search")
  }
}

module.exports = new SitesController()

//export thu gi thi nhan duoc thu do
// const newController = require('./NewsController')
