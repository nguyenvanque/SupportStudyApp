const Chatroom = require ('../models/Chatroom');
const {mongooseToObject} = require ('../../util/mongoose');

class ChatRoomController {
  //[GET],/chat/room
  show (req, res) {
    res.render ('chat/chatRoom');
  }
  //[GET],/chat/room
  createChatRoom (req, res) {
    const {name} = req.body;
    if (!name) throw 'Name không có';

    const nameRegex = /^[A-Za-z\s]+$/;
    if (!nameRegex.test (name)) throw 'Tên chat room không hợp lệ';
    const chatRoomExists = Chatroom.findOne ({name});
    if (chatRoomExists) throw 'Bạn có thể dùng tên này';
    const chatRoom = new Chatroom ({
      name,
    });
  }
  //[POST],/courses/store
  store (req, res) {
    const data = req.body;
    data.image = `https://img.youtube.com/vi/${req.body.videoId}/sddefault.jpg`;
    const course = new Courses (data);
    course.save ().then (() => res.redirect ('/')).catch (err => {
      console.log ('ERR', err);
    });
  }
  //[GET],/courses/:id/edit
  edit (req, res, next) {
    Courses.findById (req.params.id)
      .then (courses => {
        res.render ('courses/edit', {
          courses: mongooseToObject (courses),
        });
      })
      .catch (next);
  }
  //[PUT],/courses/:id
  update (req, res, next) {
    const data = req.body;
    data.image = `https://img.youtube.com/vi/${req.body.videoId}/sddefault.jpg`;

    Courses.updateOne ({_id: req.params.id}, data)
      .then (() => res.redirect ('/me/stored/courses'))
      .catch (next);
  }
  //[DELETE],/courses/:id
  delete (req, res, next) {
    Courses.deleteOne ({_id: req.params.id})
      .then (() => res.redirect ('back'))
      .catch (next);
  }
}

module.exports = new ChatRoomController ();

//export thu gi thi nhan duoc thu do
// const newController = require('./NewsController')
//GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD
//GET gửi yêu cầu từ client lên server trả về dữ liệu cho phía client
//POST gửi yêu cầu từ client lên server lưu lại 1,tạo mới một dữ liệu dữ liệu cho phía client
//PUT,PATCH gửi yêu cầu từ client lên server chỉnh sửa một dữ liệu dữ liệu
//PUT CHỉnh sửa hẳn 1 document PATCH thì chỉnh sửa từng fill
