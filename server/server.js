var express = require('express')
var multer  = require('multer')
var path = require('path')
var redis = require("redis")
var client = redis.createClient()
var geo = require('georedis').initialize(client)
var app = express()

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, './uploads')
  },
  filename: function (req, file, cb) {
    //cb(null, file.fieldname + '-' + Date.now())
    cb(null, file.fieldname + '-' + Date.now() + path.extname(file.originalname))
  }
})

var upload = multer({ storage: storage })

app.post('/upload', upload.single('file'), function (req, res, next) {
  // req.file is the `avatar` file
  // req.body will hold the text fields, if there were any
})

app.post('/photos/upload', upload.array('photos', 12), function (req, res, next) {
  // req.files is array of `photos` files
  // req.body will contain the text fields, if there were any
})

var cpUpload = upload.fields([
  { name: 'title', maxCount: 1 },
  { name: 'tags', maxCount: 1 },
  { name: 'review', maxCount: 1 },
  { name: 'rating', maxCount: 1 },
  { name: 'image', maxCount: 1 }
  ])
app.post('/save-location', cpUpload, function (req, res, next) {
  // req.files is an object (String -> Array) where fieldname is the key, and the value is array of files
  //
  // e.g.
  //  req.files['avatar'][0] -> File
  //  req.files['gallery'] -> Array
  //
  // req.body will contain the text fields, if there were any
  console.log(req.body.title);
  console.log(req.body.rating);
  console.log(req.body.address);

  geo.addLocation('singapore', {latitude: req.body.latitude, longitude: req.body.longitude}, function(err, reply){
  if(err) console.error(err)
    else console.log('added location:', reply)
  })
})

var nearbys = upload.fields([
  { name: 'longitude', maxCount: 1 },
  { name: 'latitude', maxCount: 1 },
  { name: 'address', maxCount: 1 }
  ])
app.post('/nearby', nearbys, function(req,res,next) {
  console.log(req.body.longitude);
})

app.listen(8080);
