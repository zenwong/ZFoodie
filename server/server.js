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

var cpUpload = upload.fields([
  { name: 'title', maxCount: 1 },
  { name: 'tags', maxCount: 1 },
  { name: 'review', maxCount: 1 },
  { name: 'rating', maxCount: 1 },
  { name: 'image', maxCount: 1 }
  ])
app.post('/save-location', cpUpload, function (req, res, next) {
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
		console.log(json);
		res.send(json);
	})
})

app.listen(8080);
