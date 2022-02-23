const mongoose = require("mongoose");
const Schema = mongoose.Schema;
const slug = require("mongoose-slug-generator");
mongoose.plugin(slug);

const Post = new Schema(
  {
    description: { type: String, required: true },
    image: { type: String },
    userId: { type: mongoose.Schema.Types.ObjectId, ref:"User"  },
    comment: [{type: mongoose.Schema.Types.ObjectId,ref:"Comment"}]
    // typeClassId: { type: mongoose.Schema.Types.ObjectId, required: true },
  },
  {
    timestamps: true,
  }
);
module.exports = mongoose.model("Post", Post);
