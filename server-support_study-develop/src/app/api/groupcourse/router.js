const express = require("express")

const router = express.Router()

const controller = require("./GroupCourseController")
const multer = require('multer');
fs = require('fs-extra')

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'src/uploads/group') // đường dẫn thu mục chứa hình
  },
  filename: function (req, file, cb) {
    cb(null, file.fieldname + '-' + Date.now() + '.png')
  }
})
var upload = multer({ storage: storage })


router
  .post("/api/createGroupCourse", controller.createGroupNoImage)
  .post("/api/createGroupWithImage",upload.single('group'), controller.createGroupWithImage)
  .post("/api/updateGroupImage",upload.single('group'), controller.updateGroupImage)

  .post("/api/joinGroup", controller.joinGroup)
  .post("/api/deleteUserGroup", controller.deleteUserGroup)

  .get("/api/getAllGrroup", controller.getAllGroup)
  .post("/api/getAllGroupByCourseID", controller.getAllGroupByCourseID)
  .post("/api/getAllGroupByID", controller.getAllGroupByID)
  .post("/api/updateGroupName", controller.updateGroupName)
  .post("/api/deleteGroup", controller.deleteGroup)


module.exports = router //exporst qua index.js để su dung

