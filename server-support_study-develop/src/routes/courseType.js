const express = require('express');
const router = express.Router();
const courseTypeController = require('../app/controllers/CourseTypeController');


router.get('/', courseTypeController.show);
router.get('/create', courseTypeController.create);
router.post('/store', courseTypeController.store);
router.get('/:id/edit',courseTypeController.edit);
router.put('/:id',courseTypeController.update);
router.delete('/:id',courseTypeController.delete);


module.exports = router;