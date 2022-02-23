const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const CourseType = new Schema(
    {
      name: { type: String, required: true },
      description: { type: String },
      image: { type: String },
    },
    {
      timestamps: true,
    }
  );
  module.exports = mongoose.model("CourseType", CourseType);