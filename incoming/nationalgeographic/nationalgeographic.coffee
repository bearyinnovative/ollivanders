#!/usr/bin/coffee
# you need install nodejs coffeescript and npm package : cheerio & request

request = require('request')
url = require('url')
cheerio = require('cheerio')

NG_HOME = "http://photography.nationalgeographic.com/"
NG_URL = "http://photography.nationalgeographic.com/photography/photo-of-the-day/"
YOUR_HOOK_URL = "XXXXXX" # replace with your bearychat incoming robot hook url

request NG_URL,  (error, response, body) ->
  if (!error && response.statusCode == 200)
    global.$ = cheerio.load(response.body)
    date = new Date()
    imageUrl = NG_URL +  $('.primary_photo img').attr('src')
    imageMarkdown = "![" + date.toDateString() + "](" + imageUrl + ")"
    console.log imageMarkdown

    request
      url: YOUR_HOOK_URL
      method: "POST"
      headers:
        'Content-Type': 'application/json; charset=UTF-8',
      body: JSON.stringify
        payload:
          JSON.stringify
            text: imageUrl
            attachments: [
              {
                'title': date.toDateString(),
                'text': imageMarkdown
              }
            ]
     , (error, response, body) ->
       console.log body
