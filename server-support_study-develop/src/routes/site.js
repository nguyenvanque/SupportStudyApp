const express = require ('express');
const router = express.Router();

const sitesController = require('../app/controllers/SitesController');

// newController.index;
router.get('/search',sitesController.search);
router.get('/',sitesController.index);

module.exports = router;