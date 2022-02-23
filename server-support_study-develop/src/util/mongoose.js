module.exports ={
  /** map qua để trả lại object bình thường chứ bình 
   * thường object của thằng này nằm trong proto
  */
    mutipleMongooseToObject: (mongooseArrays)=>{
      return  mongooseArrays.map(mongoose=>mongoose.toObject());
    },
    mongooseToObject:(mongoose)=>{
        return mongoose ? mongoose.toObject(): mongoose;
    }
}