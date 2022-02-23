const Courses = require("../models/Courses")
const { mongooseToObject } = require("../../util/mongoose")

class CoursesController {
  //[GET],/courses/:slug
  show(req, res) {
    Courses.findOne({ slug: req.params.slug })
      .then((course) => {
        console.log('course',course);
        res.render("courses/show", { course: mongooseToObject(course) });
      })
      .catch((err) => next(err))
  }
  //[GET],/courses/create
  create(req, res) {
    res.render("courses/create")
  }
  //[POST],/courses/store
  store(req, res) {
    const data = req.body
    // Update later
    data.image = `https://img.youtube.com/vi/${req.body.videoId}/sddefault.jpg`
    data.userId = '60bb295b1830b1b8db478bb3'
    data.typeClassId = '60bb295b1830b1b8db478bb3'
    const course = new Courses(data)
    course
      .save()
      .then(() => res.redirect("/home"))
      .catch((err) => {
        console.log("ERR", err)
      })
  }
  //[GET],/courses/:id/edit
  edit(req, res, next) {
    Courses.findById(req.params.id)
      .then((courses) => {
        res.render("courses/edit", {
          courses: mongooseToObject(courses),
        })
      })
      .catch(next)
  }
  //[PUT],/courses/:id
  update(req, res, next) {
    const data = req.body
    data.image = `https://img.youtube.com/vi/${req.body.videoId}/sddefault.jpg`

    Courses.updateOne({ _id: req.params.id }, data)
      .then(() => res.redirect("/me/stored/courses"))
      .catch(next)
  }
  //[DELETE],/courses/:id
  delete(req, res, next) {
    Courses.deleteOne({ _id: req.params.id })
      .then(() => res.redirect("back"))
      .catch(next)
  }
}

module.exports = new CoursesController()

//export thu gi thi nhan duoc thu do
// const newController = require('./NewsController')
//GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD
//GET gửi yêu cầu từ client lên server trả về dữ liệu cho phía client
//POST gửi yêu cầu từ client lên server lưu lại 1,tạo mới một dữ liệu dữ liệu cho phía client
//PUT,PATCH gửi yêu cầu từ client lên server chỉnh sửa một dữ liệu dữ liệu
//PUT CHỉnh sửa hẳn 1 document PATCH thì chỉnh sửa từng fill
