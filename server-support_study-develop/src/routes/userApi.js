const express = require ('express');
const router = express.Router();

const UserController = require('../app/controllers/UserController');

const auth = require('../middlewares/auth');

router.post('/register',UserController.apiRegister);
router.post('/login',UserController.apiLogin);

module.exports = router;