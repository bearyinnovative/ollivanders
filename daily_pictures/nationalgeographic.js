#! /usr/bin/node
http = require('http')
url = require('url')
cheerio = require('cheerio')


// 国家地理每日一图 National Geographic Photo of the day

// 照片来源 http://photography.nationalgeographic.com/photography/photo-of-the-day/

// 目测是每天下午更新


YOUR_HOOK_URL = "http://hook.bearychat.com/xxxxxxxxx" // replace with your incoming robot hook url

hookUrl = url.parse(YOUR_HOOK_URL)


var getOptions = {
  host: "photography.nationalgeographic.com",
  port: 80,
  path: '/photography/photo-of-the-day/',
  method: 'GET'
};

var request = http.request(getOptions, function (response) {
  var html = '';
  response.on('data', function (chunk) {
    html += chunk;
  });
  response.on('end', function () {
    global.$ = cheerio.load(html);
    url = "http:" + $('.primary_photo img').attr('src');
    sendImageToHook(url)
  });
});

request.end()

var sendImageToHook = function (url) {
  var date = new Date()
  var imageMarkdown = "![" + date.toDateString() + "](" + url + ")"
  var payload = {
    'text': url,
    'attachments': [
      {
        'title': date.toDateString(),
        'text': imageMarkdown
      }
    ]
  };
  var dataString = JSON.stringify(payload);

  var headers = {
    'Content-Type': 'application/json; charset=UTF-8',
  };

  var postOptions = {
    host: hookUrl.host,
    port: 80,
    path: hookUrl.path,
    method: 'POST',
    headers: headers
  };

  var req = http.request(postOptions, function(res) {
    res.setEncoding('utf-8');
    var responseString = '';
    res.on('data', function(data) {
      responseString += data;
    });
    res.on('end', function() {
      console.log(responseString);
    });
  });

  req.on('error', function(e) {
      console.log(e)
  });

  req.write(dataString);
  req.end();
}
