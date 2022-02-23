var Question = require("../models/Questions");
const {mongooseToObject, mutipleMongooseToObject } = require("../../util/mongoose")

class questionController {
  //[GET]/question
  async  index(req, res, next) {
   await Question.find({})
      .then((listQuestion) => {
        // console.log('listQuestion',listQuestion)
        //map qua de bien thang nay thanh object thuong con contructor no khong doc duoc
        res.render("question/ListQuestion", {
          listQuestion: mutipleMongooseToObject(listQuestion),
        })
      })
      .catch(next)
  }

  //[GET]/question/create
  create(req, res) {
    res.render("question/create")
  }
  //[POST],/question/add
  add(req, res) {
    const data = req.body
    const question = new Question(data)
    question
      .save()
      .then(() => res.redirect("/question"))
      .catch((err) => {
        console.log("ERR", err)
      })
      
  }
  //[GET],/question/:id/edit
  edit(req, res, next) {
    Question.findById(req.params.id)
      .then((question) => {
        res.render("question/edit", {
          question: mongooseToObject(question),
        })
      })
      .catch(next)
  }
  //[PUT],/question/:id
  update(req, res, next) {
    const data = req.body
    Question.updateOne({ _id: req.params.id }, data)
      .then(() => res.redirect("/me/stored/courses"))
      .catch(next)
  }
  //[DELETE],/question/:id
  delete(req, res, next) {
    Question.deleteOne({ _id: req.params.id })
      .then(() => res.redirect("back"))
      .catch(next)
  }

}
module.exports = new questionController ();



// exports.getAll = async function (req, res) {
//   await Question.find({}).lean().exec((err, data) => {
//     res.render("question/ListQuestion", { listQuestion: mutipleMongooseToObject(data) });
//   });
// };

// exports.insert = function (req, res) {
//     let question = new Question({
//         title: req.body.title,
//         option1: req.body.option1,
//         option2: req.body.option2,
//         option3: req.body.option3,
//         option4: req.body.option4,
//         trueOption: req.body.trueOption,

//     });
//     question.save(function (err) {
//       if (err) {
//         return next(err);
//       }
//       res.redirect('InsertQuestion')
//     });
//   };

//   exports.update = (req, res) => {

//     let id = req.body.edit_ID;
//     let _title = req.body.edit_title;
//     let _option1 = req.body.edit_option1;
//     let _option2 = req.body.edit_option2;
//     let _option3 = req.body.edit_option3;
//     let _option4 = req.body.edit_option4;
//     let _trueOption = req.body.edit_trueOption;
  
//     Question.findByIdAndUpdate(
//        {_id:id},
//        { title:_title,option1: _option1, option2: _option2, option3: _option3,option4:_option4, trueOption:_trueOption }
//     ,
//       function (err, result) {
//         if (err) {
//           console.log(err);
//         }
//         exports.getAll(req, res);
//       });
//   };


//   exports.delete = (req, res) => {
//     let id = req.body.del_ID;
//     console.log(id);
//     Question.findByIdAndRemove({ _id: id },
//       (err) => {
//         if (err) {
//           console.log(err);
//         }
//         res.redirect('ListQuestion')
//       });
//   };
  