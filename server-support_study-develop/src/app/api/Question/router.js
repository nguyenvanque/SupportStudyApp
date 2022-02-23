const express = require("express")

const router = express.Router()

const controller = require("./controller")
const qscontroller = require("../../controllers/QuestionController")

//getAll
router.get("/", controller.getAll)
router.post("/add", qscontroller.add)

module.exports = router //exporst qua index.js để su dung
