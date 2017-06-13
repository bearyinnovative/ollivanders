text = json.parse(request.body).text
email = string.sub(text, string.find(text,"%s.+$"))
email = string.gsub(email , "%s", "")

function _sort (a,b)
  return a.requestTime > b.requestTime
end

apiUser = "YOUR_SENDCLOUD_API_USER"
apiKey = "YOUR_SENDCLOUD_API_KEY"

if email then
  response = http.request {
    url="http://api.sendcloud.net/apiv2/data/emailStatus?apiUser="..apiUser.."&apiKey="..apiKey.."&days=7&email="..email
  }
  status = json.parse(response.content).info
  if status.total == nil then
    return {text="最近一周没有向 "..email.." 发送邮件"}
  end
  text = "最近一周向 "..email.." 发送了 "..status.total.." 封邮件"
  attachments = {}
  table.sort(status.voList, _sort)
  for key, value in pairs(status.voList) do
    log = "**"..value.status.."** "..value.requestTime..((value.status ~= "送达" and "\n"..value.sendLog) or "")
    color = (value.status ~= "送达" and "#E15252") or "#29C4CC"
    attachments[key] = {
      text=log,
      color=color
    }
  end
  return {
    text=text,
    attachments=attachments
  }
else
  return {text="输入格式不对，正确示例： 触发词 loddit@gmail.com"}
end
