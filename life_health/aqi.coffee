request = require('request')
url = require('url')
cheerio = require('cheerio')

APICN_URL = "http://aqicn.org/city/beijing/cn"
YOUR_HOOK_URL = "http://hook.bearychat.com/xxxxxxxxx" # replace with your incoming robot hook url
request APICN_URL,  (error, response, body) ->
  if (!error && response.statusCode == 200)
    global.$ = cheerio.load(response.body)
    date = new Date()
    aqi = $('.api .aqivalue').text()
    update = $(".aqiwidget .aqivalue").last().next().next().text()
    text = "北京当前 AQI 指数是 **#{aqi}** #{update} | 数据来源： #{APICN_URL}"
    console.log text
    request
      url: YOUR_HOOK_URL
      method: "POST"
      headers:
        'Content-Type': 'application/json; charset=UTF-8',
      body: JSON.stringify
        payload:
          JSON.stringify
            text: text
    , (error, response, body) ->
      console.log body

