var User = require("../../models/User");
fs = require('fs-extra')

exports.editImage = function (req, res)  {
  let id = req.body._id;
  var new_image=req.file.filename
  var old_image=req.body.old_image
  User.findByIdAndUpdate(
    { _id: id }, { image: `http://localhost:3000/profile/${new_image}` }
    ,
    function (err, result) {
      if (err) {
        console.log(err);
      }
      if(old_image!="noImage"){
       var path=old_image.slice(30,old_image.length)
        fs.unlinkSync("src/uploads/users/"+ path)
      }
      res.json(result)
    });
}

  
exports.deleteUser = (req, res) => {
  let id = req.body._id;
  User.findByIdAndRemove({ _id: id },
    (err,result) => {
      if (err) {
        console.log(err);
      }
      try {
        fs.unlinkSync("src/uploads/users/"+ req.body.image_path)
      } catch (error) {
        console.log(error)
      }
      res.json(result)
    });
};


exports.register = function (req, res) {
  let user = new User({
    name: req.body.name,
    email: req.body.email,
    password: req.body.password,
    image: req.body.image,
    isTurtor: req.body.isTurtor,
  });
  user.save(function (err) {
    res.json(user)
  });
};
exports.getAllUser = async function (req, res) {
  await User.find({}).lean().exec((err, data) => {
    res.json(data);
  });
};
exports.findUser = async function (req, res) {
  await User.findOne({ email: req.body.email, password: req.body.password }).lean().exec((err, data) => {
    if (data!= null) {
      res.json(data)
    }else{
      res.json({code:300})
    }
  
  })
}

exports.findUserId = (req, res) => {
  User.find({ _id: req.body._id })
    .exec(function (err, data) {
      if (err) {
        if (err.kind === 'ObjectId') {
          return res.status(404).send({
            message: "Not found  Id " + req.params.subjectId
          });
        }
        return res.status(500).send({
          message: "Error retrieving Student with given subject Id " + req.params.subjectId
        });
      }

      res.send(data);
    });
};

exports.updatePassword = (req, res) => {
  let id = req.body._id;
  let password = req.body.password;
  User.findByIdAndUpdate(
    { _id: id }, { password: password }
    ,
    function (err, result) {
      if (err) {
        console.log(err);
      }
      res.json(result)
    });
};
exports.updateName = (req, res) => {
  let name = req.body.name;
  User.findByIdAndUpdate(
    {_id:req.body._id}, { name: name }
    ,
    function (err, result) {
      if (err) {
        console.log(err);
      }
      res.json(result)
    });
};


exports.deleteUser = (req, res) => {
  let id = req.body._id;
  User.findByIdAndRemove({ _id: id },
    (err) => {
      if (err) {
        console.log(err);
      }
    });
};