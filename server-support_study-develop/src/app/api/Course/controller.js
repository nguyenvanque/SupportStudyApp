var Courses = require("../../models/Courses")

exports.getAll = async function (req, res) {
  const payload = await Courses.find().populate('courseType','name description').populate('userId','name image')
  res.status(200).json(payload)
}

exports.getById = async function (req, res) {
  try {
    const payload = await Courses.findById(req.params.id)

    if (!payload) {
      res.status(404).json({
        error: "Resource not found!!",
      })
    }
    res.status(200).json({
      payload,
    })
  } catch (error) {
    res.status(500).json({
      error: error.message,
    })
  }
}

exports.store = async function (req, res) {
  const data = req.body
  // Update later
  // data.image = `https://img.youtube.com/vi/${req.body.videoId}/sddefault.jpg`
  // data.userId = "60bb295b1830b1b8db478bb3"
  const course = new Courses(data)
  try {
    const payload = await course.save()

    res.status(200).json({ payload })
  } catch (error) {
    console.log("ERR", err)
  }
}

exports.update = async function (req, res) {
  try {
    const payload = await Courses.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    })

    if (!payload) {
      res.status(404).json({
        error: "Resource not found!!",
      })
    }
    res.status(200).json({
      payload,
    })
  } catch (error) {
    res.status(500).json({
      error: error.message,
    })
  }
}

exports.delete = async function (req, res) {
  const payload = await Courses.findById(req.params.id)

  if (!payload) {
    res.status(404).json({
      error: "Resource not found!!",
    })
  }

  try {
    await payload.delete()
    res.status(200).json({
      payload,
    })
  } catch (error) {
    res.status(500).json({
      error: error.message,
    })
  }
}
