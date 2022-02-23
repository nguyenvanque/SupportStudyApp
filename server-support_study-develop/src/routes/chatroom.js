const express = require ('express');
const router = express.Router();

const chatRoomController = require('../app/controllers/ChatRoomController');

const auth = require('../middlewares/auth')

// newController.index;

router.get('/:slug',auth,chatRoomController.show);
router.post('/room',auth,chatRoomController.createChatRoom);


module.exports = router;