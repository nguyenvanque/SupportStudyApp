const mongoose = require ('mongoose');
const Schema = mongoose.Schema;
const slug = require('mongoose-slug-generator');
mongoose.plugin(slug)

const ChatroomSchema = new Schema (
  {
    name: {type: String},
  }
);
module.exports = mongoose.model ('Chatroom', ChatroomSchema);