const mongoose = require('mongoose');
const Schema = mongoose.Schema;

let QuestionSchema = new Schema({
    title: String,
    option1: String,
    option2: String,
    option3: String,
    option4: String,
    trueOption: Number,
});
module.exports = mongoose.model('Questions', QuestionSchema);