var Question = require("../../models/Questions")

exports.getAll = async function (req, res) {
  await Question.find({})
    .lean()
    .exec((err, data) => {
      res.json(data)
    })
}
