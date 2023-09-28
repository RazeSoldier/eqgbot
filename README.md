## 配置
### 全局配置
#### QQ登录
**QQ账号**
id为QQ号，password为密码（当选择非二维码登录时必须提供）
```json
{
  "account": {
    "id": 123,
    "password": "test"
  }
}
```
**登录方法**  
仅允许的值为`qrcode`，代表使用二维码登录。如果未指定则默认使用密码登录。
```json
{
  "loginMethod": "qrcode"
}
```
**登录设备**  
允许的值为：`pad`, `phone`, `ipad`, `macos`，默认使用手表协议
```json
{
  "loginProtocol": "pad"
}
```
## 许可证
本机器人基于Mirai机器人框架编写。
使用AGPLv3 with Mamoe Exceptions许可证。
* 所有衍生软件必须使用相同协议 
(AGPLv3 with Mamoe Exceptions) 开源
* 本软件禁止用于一切商业活动
* 本软件禁止收费传递, 或在传递时不提供源代码