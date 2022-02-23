const express = require ('express');
const router = express.Router();

const userController = require('../app/controllers/UserController');

// newController.index;

router.post('/login',userController.loginStore);
router.post('/register',userController.register);
router.get('/logup',userController.dangky);
router.get('/',userController.login);
router.delete('/:id',userController.delete);


module.exports = router;