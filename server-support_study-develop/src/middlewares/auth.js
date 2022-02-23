const jwt = require ('jsonwebtoken');

module.exports = async (req, res, next) => {
  const authHeader = req.header('Authorization');
  
  const token = authHeader && authHeader.split (' ')[1];

  if(!token) return res.status(401).json({success:false,message:'Chưa có token'});
  try {
    const payload = jwt.verify (token, process.env.ACCESS_TOKEN_SECRET);
    req.userId = payload.userId;
    next ();
  } catch (error) {
      res.status(401).json({
          message:"Token lởm"
      })
  }
};
