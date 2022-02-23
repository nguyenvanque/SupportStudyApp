const mongoose = require("mongoose")
const Schema = mongoose.Schema
const slug = require("mongoose-slug-generator")
mongoose.plugin(slug)

const Course = new Schema(
  {
    name: { type: String, required: true },
    description: { type: String },
    image: { type: String },
    userId: { type: mongoose.Schema.Types.ObjectId, required: true, ref: "User" },
    courseType: {
      type: mongoose.Schema.Types.ObjectId,
      required: true,
      ref: "CourseType",
    },
    slug: { type: String, slug: "name", unique: true },
    level: { type: String },
  },
  {
    timestamps: true,
  }
)



module.exports = mongoose.model("Course", Course)
