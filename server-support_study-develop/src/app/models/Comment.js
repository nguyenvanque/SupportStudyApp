const mongoose = require("mongoose")
const Schema = mongoose.Schema

const Comment = new Schema(
  {
    content: { type: String, required: true },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
    // typeClassId: { type: mongoose.Schema.Types.ObjectId, required: true },
  },
  {
    timestamps: true,
  }
)
module.exports = mongoose.model("Comment", Comment)
