const User = require ('../models/User');

const {mongooseToObject} = require ('../../util/mongoose');
const sha256 = require ('js-sha256');
const jwt = require ('jsonwebtoken');
const slug = require("mongoose-slug-generator");


class UserController {
  //[GET],/
  dangky (req, res) {
    res.render ('login-register-custom', {layout: false});
  }
  login (req, res) {
    res.render ('login', {layout: false});
  }

  //[POST],/register

  async register (req, res, next) {
    const {name, email, password, isTurtor, image} = req.body;
    const emailRegex = /@gmail.com|@yahoo.com/;

    if (!emailRegex.test (email)) res.json ({error: 'Khong ho tro domain'});

    if (password.length < 6) res.json ({error: 'Pass chứa 6 ký tự'});

    try {
      const checkExits = await User.findOne ({
        email: email,
      });
      if (checkExits) return res.json ({error: 'Trùng Mail rồi bạn'});

      const user = new User ({
        name,
        email,
        password: sha256 (password + process.env.SALT),
        isTurtor,
        image,
      });
      await user.save ();

      res.redirect ('/home');
    } catch (error) {
      console.log ('ERRRRRORRR', error);
    }
  }

  //[POST]/login

  async loginStore (req, res) {
    const {email, password} = req.body;

    if (!email || !password)
      return res
        .status (400)
        .json ({success: false, message: 'Sai mail hoặc mật khẩu'});

    const user = await User.findOne ({
      email: email,
      password: sha256 (password + process.env.SALT),
    });

    if (!user)
      return res.json ({success: false, message: 'Sai mail hoặc mật khẩu'});

    res.redirect ('./home');
  }

  delete (req, res, next) {
    User.deleteOne ({_id: req.params.id})
      .then (() => res.redirect ('back'))
      .catch (next);
  }

  //[POST]/api/user
  async apiLogin (req, res) {
    const {email, password} = req.body;

     
    const user = await User.findOne ({
      email: email,
      password: sha256 (password + process.env.SALT),
    });
    try {
      if (!user) {
        res.json ({success: false, message: 'Sai mail hoặc mật khẩu'});
      } else {
        const accessToken = jwt.sign (
          {userId: user._id},
          process.env.ACCESS_TOKEN_SECRET
        );
        res.json ({success: true, message: 'success', accessToken});
      }
    } catch (error) {
      res.json ({success: false, message: 'Lỗi server'});
    }
  }

  //[POST]/api/user
  async apiRegister (req, res, next) {
    const {name, email, password, isTurtor, image} = req.body;
    
    const emailRegex = /@gmail.com|@yahoo.com/;

    if (!emailRegex.test (email)) res.json ({error: 'Khong ho tro domain'});

    if (password.length < 6) res.json ({error: 'Pass chứa 6 ký tự'});

    try {
      const checkExits = await User.findOne ({
        email: email,
      });
      if (checkExits) return res.json ({error: 'Trùng Mail rồi bạn'});
      const user = new User ({
        name,
        email,
        password: sha256 (password + process.env.SALT),
        image,
        isTurtor,
      });
      await user.save ();
      const accessToken = jwt.sign (
        {userId: user._id},
        process.env.ACCESS_TOKEN_SECRET
      );
      res.json ({success: true, message: 'success', accessToken});
    } catch (error) {
      console.log ('ERRRRRORRR', error);
    }
  }
}

module.exports = new UserController ();

//export thu gi thi nhan duoc thu do
// const newController = require('./NewsController')
//GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD
//GET gửi yêu cầu từ client lên server trả về dữ liệu cho phía client
//POST gửi yêu cầu từ client lên server lưu lại 1,tạo mới một dữ liệu dữ liệu cho phía client
//PUT,PATCH gửi yêu cầu từ client lên server chỉnh sửa một dữ liệu dữ liệu
//PUT CHỉnh sửa hẳn 1 document PATCH thì chỉnh sửa từng fill
