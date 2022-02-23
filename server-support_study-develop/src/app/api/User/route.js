const express = require("express")
const router = express.Router()
const controller = require("./UserController")
const multer = require('multer');
fs = require('fs-extra')

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'src/uploads/users') // đường dẫn thu mục chứa hình
  },
  filename: function (req, file, cb) {
    cb(null, file.fieldname + '-' + Date.now() + '.png')
  }
})

var upload = multer({ storage: storage })

router
  .post("/api/register", controller.register)
  .post("/api/findUserEmailPassWord", controller.findUser)
  .get("/api/getAllUser", controller.getAllUser)
  .post("/api/getAllUseId", controller.findUserId)
  .post("/api/updatePassword", controller.updatePassword)
  .post("/api/updateName", controller.updateName)
  .post("/api/updateImage",upload.single('user'), controller.editImage)
  .post("/api/deleteUser", controller.deleteUser)
module.exports = router //exporst qua index.js để su dung