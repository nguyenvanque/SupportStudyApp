var CourseType = require("../../models/CourseType")

exports.store = async function (req, res) {
  const data = req.body

  const courseType = new CourseType(data)
  try {
    const payload = await courseType.save()

    res.status(200).json({ payload })
  } catch (error) {
    console.log("ERR", err)
  }
}

exports.getAll = async function (req, res) {
    const payload = await CourseType.find()
    res.status(200).json(payload)
  }
