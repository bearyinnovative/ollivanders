# Description:
#   Example scripts for you to examine and try out.
#
# Commands:
#   hubot chisha - which restaurant to enjoy a small dinner
#   hubot qingke - which restaurant to enjoy a big dinner

module.exports = (robot) ->

  bigRestaurants = ['汉口街头', '院落', '汤城小厨']

  smallRestaurants = ['KFC', ' 麦当劳', '阿香米线', '食其家']

  robot.respond /chisha/i, (res) ->
    res.send res.random smallRestaurants

  robot.respond /qingke/i, (res) ->
    res.send res.random bigRestaurants
