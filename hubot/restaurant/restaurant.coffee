# Description:
#   Example scripts for you to examine and try out.
#
# Commands:
#   hubot chisha - which restaurant to enjoy

module.exports = (robot) ->

  bigRestaurants = ['汉口街头', '院落', '汤城小厨', '渝乡人家', '西贝莜面村' ,'便宜坊', '新辣道' ]
  smallRestaurants = ['潮汕渔家', 'KFC', ' 麦当劳', '80诱惑', '阿拉兰牛肉面', '阿香米线', '食其家', '和合谷', '亚惠美食', '陕西面馆', '回转寿司' ]

  robot.respond /chisha/i, (res) ->
    res.send res.random smallRestaurants

  robot.respond /qingke/i, (res) ->
    res.send res.random bigRestaurants
