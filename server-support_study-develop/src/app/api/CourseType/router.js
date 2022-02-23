const express = require("express")
const router = express.Router()
const controller = require("./controller")

router
  .get("/", controller.getAll)
//   .get("/:id", controller.getById)
  .post("/", controller.store)
//   .put("/:id", controller.update)
//   .delete("/:id", controller.delete)
module.exports = router //exporst qua index.js để su dung
