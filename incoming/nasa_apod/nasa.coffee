request = require('request')
url = require('url')
cheerio = require('cheerio')

NASA_APOD_URL = "http://apod.nasa.gov/"
YOUR_HOOK_URL = "http://hook.bearychat.com/xxxxxxxxx" # replace with your incoming robot hook url

request NASA_APOD_URL,  (error, response, body) ->
  if (!error && response.statusCode == 200)
    global.$ = cheerio.load(response.body)
    date = new Date()
    imageUrl = NASA_APOD_URL + $('center img').attr('src')
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

