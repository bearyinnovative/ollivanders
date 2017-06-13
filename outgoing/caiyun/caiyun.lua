text = json.parse(request.body).text
loc = string.sub(text, string.find(text,"%s.+$"))
loc = string.gsub(loc , "%s", "")
caiyunToken = "YOUR_CAIYUN_API_KEY"
weather_map = {
  ["CLEAR_DAY"]="晴 :sunny:",
  ["CLEAR_NIGHT"]="晴 :moon:",
  ["PARTLY_CLOUDY_DAY"]="多云 :cloud:",
  ["PARTLY_CLOUDY_NIGHT"]="多云 :cloud:",
  ["CLOUDY"]="阴 :partly_sunny:",
  ["RAIN"]="雨 :rain_cloud:",
  ["SNOW"]="雪 :snow_cloud:",
  ["WIND"]="大风 :wind_blowing_face:",
  ["FOG"]="雾 :fog:",
  ["HAZE"]="霾 :fog:"
}

if loc then
  if storage["lng_"..loc] and storage["lat_"..loc] then
    lng = storage["lng_"..loc]
    lat = storage ["lat_"..loc]
  else
    response = http.request {
      url="https://maps.googleapis.com/maps/api/geocode/json?address="..loc
    }
    lat = json.parse(response.content)['results'][1]['geometry']['location']['lat']
    lng = json.parse(response.content)['results'][1]['geometry']['location']['lng']
    storage["lng_"..loc] = lng
    storage["lat_"..loc] = lat
  end
  response2 = http.request {
    url="https://api.caiyunapp.com/v2/"..caiyunToken.."/"..lng..","..lat.."/realtime.json"
  }
  temperature = json.parse(response2.content)['result']['temperature']
  pm25 = json.parse(response2.content)['result']['pm25']
  humidity = json.parse(response2.content)['result']['humidity'] * 100
  weather = weather_map[json.parse(response2.content)['result']['skycon']]
  texti = loc.."现在天气是"..weather.." 温度"..temperature.."度 PM2.5为"..pm25.." 相对湿度"..humidity.."%"
  return {text=text}
else
  return {text="输入格式不对，正确示例： 将台路小学"}
end
