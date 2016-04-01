# Description:
#   answer your question
#
# Commands:
#   hubot ask something - answer your question

module.exports = (robot) ->

  # replace your key and user_id
  robot.respond /ask (.+)/i, (res) ->
    info= res.match[1]
    url = "http://apis.baidu.com/turing/turing/turing?key=YOUR_KEY&userid=YOUR_USER_ID&info=#{info}"
    robot.http(url)
         .header('apikey', 'YOUR_KEY')
         .get() (err, resp, body) ->
           if err
             res.reply "Sorry, ERROR somewhere -_-"
             return
           { text } = JSON.parse body
           res.reply text if text
