const mongoose = require ('mongoose');
const Schema = mongoose.Schema;

const UserSchema = new Schema (
  {
    name: {type: String},
    email: {type: String, required: true},
    password: {type: String, required: true},
    image: {type: String},
    isTurtor: {type: Boolean},
  },
  {
    timestamps: true,
  }
);
module.exports = mongoose.model ('User', UserSchema);
