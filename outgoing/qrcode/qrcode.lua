function urlencode(str)
  if (str) then
    str = string.gsub (str, "\n", "\r\n")
    str = string.gsub (str, "([^%w ])",
    function (c) return string.format ("%%%02X", string.byte(c)) end)
    str = string.gsub (str, " ", "+")
  end
  return str
end

text = json.parse(request.body).text
url = string.sub(text, string.find(text,"%s.+$"))
url = string.gsub(url , "%s", "")
encoded_url = urlencode(url)
if url then
  return {
    text=url,
    attachments={
      {
        images={
          {
            url="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="..encoded_url,
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
