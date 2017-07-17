var express = require('express')
var multer  = require('multer')
var path = require('path')
var redis = require("redis")
var client = redis.createClient()
var geo = require('georedis').initialize(client)
var app = express()
app.use(express.static(__dirname + '/uploads'))

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, './uploads')
  },
  filename: function (req, file, cb) {
    //cb(null, file.fieldname + '-' + Date.now() + path.extname(file.originalname))
		cb(null, file.originalname + '.jpg')
  }
})

var upload = multer({ storage: storage })

var cpUpload = upload.fields([
  { name: 'title', maxCount: 1 },
  { name: 'tags', maxCount: 1 },
  { name: 'review', maxCount: 1 },
  { name: 'rating', maxCount: 1 },
  { name: 'image', maxCount: 1 }
  ])
app.post('/save-location', cpUpload, function (req, res, next) {
	client.geoadd('geo:locations', req.body.longitude, req.body.latitude, req.body.address);
	client.hset('images', req.body.address, req.files.image[0].filename);
	client.hset(req.body.address, 'title', req.body.title);
	client.hset(req.body.address, 'tags', req.body.tags);
	client.hset(req.body.address, 'rating', req.body.rating);
	client.hset(req.body.address, 'review', req.body.review);
})

var nearbys = upload.fields([
  { name: 'longitude', maxCount: 1 },
  { name: 'latitude', maxCount: 1 },
  { name: 'address', maxCount: 1 }
  ])
app.post('/nearby', nearbys, function(req,res,next) {
 var
	latitude  = req.body.latitude,    // req.body contains the post values
	longitude = req.body.longitude;
  
	client.georadius('geo:locations', req.body.longitude, req.body.latitude, '5', 'km', 'WITHCOORD', 'WITHDIST', 'ASC', function(err, results) {
		results = results.map(function(aResult) {
			var	resultObject = {
				address   : aResult[0],
				distance  : parseFloat(aResult[1]),
				longitude : parseFloat(aResult[2][0]),
				latitude  : parseFloat(aResult[2][1])
			};

			return resultObject;
		})
		var json = JSON.stringify(results);
		res.send(json);
	})
})

app.post('/place', function(req, res, next) {
 client.hgetall(req.body.address, function(err, results){
		res.send(results);
 }) 
})

app.listen(8080);
