text = json.parse(request.body).text
URL = string.sub(text, string.find(text,"%s.+$"))
URL = string.gsub(URL , "%s", "")
if URL then
  return {
    text=URL,
    attachments={
      {
        images={
          {
            url="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="..URL,
            height=200,
            width=200
          }
        }
      }
    }
  }
else
  return {
    text="输入格式不对，正确示例： 触发词 url"
  }
end
