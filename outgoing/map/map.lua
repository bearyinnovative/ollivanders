text = json.parse(request.body).text
LOC = string.sub(text, string.find(text,"%s.+$"))
LOC = string.gsub(LOC , "%s", "")
if LOC then
  return {
      text=LOC.."'s map [Go To Google Maps](http://www.google.cn/maps/search/"..LOC..")",
      attachments={
        {
          images={
            {
              url="http://www.google.cn/maps/api/staticmap?center="..LOC.."&size=400x400",
              height=400,
              width=400
            }
          }
        }
      }
    }
else
  return {text="输入格式不对，正确示例： 触发词 北京"}
end
