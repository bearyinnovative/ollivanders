# 彩云天气 #

彩云天气机器人，是用于检查任意位置在 [彩云天气](http://caiyunapp.com/) 里的实时天气情况的。

### 部署方式 ###

这个 lua 脚本是在 webscript.io 这个云服务上运行的。
在 webscript.io 创建一个 script 把代码复制过去即可 。
需要将代码里的 caiyunToken 替换成自己在彩云天气里申请的真实 Token。

### 使用方式 ###
1. 在 BearyChat 里添加一个 outgoing 机器人
2. 填写好触发关键词比如「$caiyun」
3. 然后将你在 webscript.io 的脚本地址复制到机器人「POST请求地址」里
4. 保存这个机器人，这时你就可以回到聊天主界面输入 「$caiyun 三元桥」 机器人就会返回三元桥在当前的最新天气情况。

ps. 机器人的原理是先由 Google geocode API 将地址名称转化为坐标，再由坐标获取对应的天气数据，会有两次请求（并且一个是海外的 API），所以可能会引起机器人超时。但是由于加入了坐标的缓存机制，通常第二次请求都是可以正常工作的。如代码所见，坐标缓存使用 `lat_地址`/`lng_地址` 做 storage 的 key。
