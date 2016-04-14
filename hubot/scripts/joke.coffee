# Description:
#   Tell you a funny joke
#
# Commands:
#   hubot joke - tell you a fresh joke

module.exports = (robot) ->

  robot.hear /joke/i, (res) ->
    robot.http("http://tambal.azurewebsites.net/joke/random")
         .get() (err, resp, body) ->
           if err
             res.send "Sorry, ERROR somewhere -_-"
             return
           { joke } = JSON.parse body
           res.send joke if joke
