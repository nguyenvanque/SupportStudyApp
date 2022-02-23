const mongoose = require("mongoose")
const Schema = mongoose.Schema
const slug = require("mongoose-slug-generator")
mongoose.plugin(slug)

const Participant  = new Schema(
  {
    jointime: { type: String, required: true },
    uid: { type: String },
  }
)
module.exports = mongoose.model("Participant", Participant)