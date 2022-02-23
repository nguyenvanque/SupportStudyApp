"use strict";
const express = require ('express');
const http = require ('http');
const path = require ('path');
const cors = require('cors');
const morgan = require ('morgan');
const exphbs = require ('express-handlebars');
const methodOverride = require('method-override');
const socketio = require('socket.io');
const app = express ();
const PORT = process.env.PORT || 3000;
const route = require ('./routes');
//import db
const db = require ('./config/db');
var groupCourseController=require('./app/api/groupcourse/router')

const server = http.createServer(app);
const io = socketio(server);
//connnect to db
db.connect ();

app.use('/profile', express.static('src/uploads/users/'));
app.use('/group', express.static('src/uploads/group/'));
app.use(methodOverride('_method'));
app.use(cors());
//io
io.on('connection',(socket)=>{
  console.log('we have a new connection')
})

//public nhung file public
app.use (express.static (path.join ('src', 'public')));
app.use (express.static (path.join ('src', 'resources/assets')));
// console.log(__dirname,'public');
//http loger
// app.use (morgan ());
app.use (
  express.urlencoded ({
    extended: true,
  })
);
app.use (express.json ());
app.use(groupCourseController)


//template engine
app.engine (
  'hbs',
  exphbs ({
    extname: '.hbs',
    //sử dụng function trong express handlerbar 
    helpers: {
      sum: (a, b) => a + b,
      dateFormat: require('handlebars-dateformat'),
    },
  })
);
app.set ('view engine', 'hbs');
app.set ('views', path.join (__dirname, 'resources', 'views'));
// console.log(path.join(__dirname,'resources/views'));
//routes khoi tao tuyen duong
route (app);

//127.0.0.1 --- localhost

server.listen (PORT, () => console.log(`Server sẵn sàng với port là ${PORT}`));
