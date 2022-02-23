const express = require ('express');
const router = express.Router();

const meController = require('../app/controllers/MeController');

// newController.index;
router.get('/stored/courses',meController.storedCourses);
router.get('/stored/user',meController.userApp);
router.get('/stored/post',meController.postApp);


module.exports = router;