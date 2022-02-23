const express = require('express');
const router = express.Router();
const questionController = require('../app/controllers/QuestionController');


router.get('/', questionController.index);
router.get('/create', questionController.create);
router.post('/store', questionController.add);
router.get('/:id/edit',questionController.edit);
router.put('/:id',questionController.update);
router.delete('/:id',questionController.delete);


module.exports = router;