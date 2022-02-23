const Comment = require("../../models/Comment")
const Post = require("../../models/Post")
const PostController = require("../../controllers/PostController")
exports.addComment = async function (req, res) {
  const data = req.body
  const comment = new Comment(data)
  try {
    const payload = await comment
      .save()
      .then(() => Post.findById(req.body.postId))
      .then((post) => {
        post.comment.unshift(comment)
        return post.save()
      })
    res.status(200).json({ payload })
  } catch (error) {
    console.log("ERR", error)
  }
}

exports.getAllPost = async function (req, res) {
  const payload = await Post.find()
    .populate([
      {
        path: "comment",
        model: "Comment",
        select: "content",
        populate: {
          path: "userId",
          model: "User",
          select: "name image",
        },
      },
    ])
    .populate("userId", "name image")
  res.status(200).json({
    payload,
  })
}

exports.createPost = async function (req, res) {
  var post_image=req.file.filename
  let post = new Post({
      description: req.body.description,
      image: `http://localhost:3000/post/${post_image}`,
      userId: req.body.userId,
  });
  try {
    const payload = await post.save()
    res.status(200).json({ payload })
  } catch (error) {
    console.log("ERR", err)
  }
}

exports.getById = async function (req, res) {
  try {
    const payload = await Post.findById(req.body.id)
      .populate([
        {
          path: "comment",
          model: "Comment",
          select: "content",
          populate: {
            path: "userId",
            model: "User",
            select: "name image",
          },
        },
      ])
      .populate("userId", "name image")

    if (!payload) {
      res.status(404).json({
        error: "Resource not found!!",
      })
    }
    res.status(200).json({
      payload,
    })
  } catch (error) {
    res.status(500).json({
      error: error.message,
    })
  }
}

exports.deletePost = PostController.delete

exports.updatePost = PostController.update
