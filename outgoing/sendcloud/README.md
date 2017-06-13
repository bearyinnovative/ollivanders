# SendCloud #

SendCloud 机器人，是用于检查使用 [SendCloud](http://sendcloud.sohu.com/) 发送邮件状态的。

### 部署方式 ###

这个 lua 脚本是在 webscript.io 这个云服务上运行的。
在 webscript.io 创建一个 script 把代码复制过去即可 。
需要将代码里的 API USER 和 API KEY 替换成自己在 SendCloud 里所使用的真实数据。

### 使用方式 ###
1. 在 BearyChat 里添加一个 outgoing 机器人
2. 填写好触发关键词比如「$sendcloud」
3. 然后将你在 webscript.io 的脚本地址复制到机器人「POST请求地址」里
4. 保存这个机器人，这时你就可以回到聊天主界面输入 「$sendcloud your@email.com」 机器人就会立即回复最近一周，像这个邮箱的投递情况。
