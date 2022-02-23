const express = require ('express');
const router = express.Router();

const postController = require('../app/controllers/PostController');

const auth = require('../middlewares/auth')

// newController.index;

// router.get('/',postController.create);
router.post('/',auth,postController.create);
router.post('/admin',postController.adminCreate);
router.get('/storeId',auth,postController.getById);
router.put('/:id',auth,postController.update);
router.delete('/admin/:id',postController.adminDelete);
router.delete('/:id',auth,postController.delete);


module.exports = router;