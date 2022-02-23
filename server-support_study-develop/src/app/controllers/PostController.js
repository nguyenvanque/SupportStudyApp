const Post = require ('../models/Post');
const {mutipleMongooseToObject} = require ('../../util/mongoose');
const {json} = require ('express');

class PostController {
  //[POST],/post/
  async create (req, res, next) {
    const {title, description, image, videoId} = req.body;
    if (!title)
      return res
        .status (400)
        .json ({success: false, message: 'title để trống'});

    try {
      const newPost = new Post ({
        title,
        description,
        image,
        videoId,
        userId: req.userId,
      });
      await newPost.save ();
      res.json ({success: true, message: 'Happy learning'});
    } catch (error) {
      res.json ({error: error});
    }
  }
  //[GET],/post/

  async getById (req, res, next) {
    console.log ('req.userId', req.userId);
    try {
      const post = await Post.find ({userId: req.userId}).populate ('userId').populate('comment');
      res.json ({success: true, post});
    } catch (error) {
      res.json ({error: error});
    }
  }
  //[PUT],/:id

  async update (req, res, next) {
    const description = req.body;

    if (!description)
      return res
        .status (400)
        .json ({success: false, message: 'title để trống'});

    try {
      const newPost = {
        description
      };
      const postUpdateCondition = {_id: req.params.id, userId: req.userId};
      const updateNew = await Post.findOneAndUpdate (
        postUpdateCondition,
        newPost,
        {new: true}
      );
      if (!updateNew)
        return res.json ({success: false, message: 'Update fail'});
      res.json ({success: true, updateNew});
    } catch (error) {
      res.json ({error: error});
    }
  }

  async delete (req, res) {
    try {
      const postDeleteCondition = {_id: req.params.id, userId: req.userId};
      const deleteOne = await Post.findOneAndDelete (postDeleteCondition);
      if (!deleteOne)
        return res.json ({success: false, message: 'delete Fail'});
      res.json ({success: true, message: 'delete thanh cong'});
    } catch (error) {
      res.json ({message: error});
    }
  }


    //[POST],/post/admin
    async adminCreate (req, res, next) {
      const {title, description, image} = req.body;
      if (!title)
        return res
          .status (400)
          .json ({success: false, message: 'title để trống'});
  
      try {
        const newPost = new Post ({
          title,
          description,
          image,
        });
        await newPost.save ();
        res.redirect ('/me/stored/post');
      } catch (error) {
        res.json ({error: error});
      }
    }
  async adminDelete (req, res) {
    try {
      const deleteOne = await Post.findOneAndDelete ({_id: req.params.id});
      if (!deleteOne)
        return res.json ({success: false, message: 'delete Fail'});
      res.redirect ('back');
    } catch (error) {
      res.json ({message: error});
    }
  }

}

module.exports = new PostController ();

//export thu gi thi nhan duoc thu do
// const newController = require('./NewsController')
