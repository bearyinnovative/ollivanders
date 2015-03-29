# Currency #

实时汇率机器人，在 BearyChat 里快速获取外币等值人民币是多少？

### 部署方式 ###

这个 lua 脚本是在 webscript.io 这个云服务上运行的。
在 webscript.io 创建一个 script 把代码复制过去即可 。

汇率数据感谢由 http://www.freecurrencyconverterapi.com/ 提供的 API，如果大家喜欢可以捐他一杯咖啡~


### 使用方式 ###
1. 在 BearyChat 里添加一个 outgoing 机器人
2. 填写好触发关键词比如「$$」
3. 然后将你在 webscript.io 的脚本地址复制到机器人「POST请求地址」里
4. 保存这个机器人，这时你就可以回到聊天主界面输入 「$$ 100 USD」 机器人就会立即回复你 100 美元目前折合人民币的数额。
5. 目前仅支持使用货币代码，举例 USD 美元，JPY 日元，HKD 港币 具体看这里 http://zh.wikipedia.org/wiki/ISO_4217

效果截图:

![screenshot](currency.png)

(现在 webscript.io 提供 7 天免费试用，如果你不想付费可以使用我部署的脚本 http://loddit.webscript.io/currency 这个脚本会和这里的代码保持同步)
