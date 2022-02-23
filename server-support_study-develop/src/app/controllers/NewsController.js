class NewsController {

  //[GET],/news
  index (req, res) {
    res.render ('home');
  }
  //[GET],/news/:slug
  show(req,res){
    res.send("NEWS DETAIL!!!");
  }
}

module.exports = new NewsController ();

//export thu gi thi nhan duoc thu do
// const newController = require('./NewsController')