# **ServerInfo**
[下载地址](https://ci.lanink.cn/view/SmallasWater/job/ServerInfo/)
- Nukkit服务器的跨服菜单
- 显示服务器的在线人数

## 使用介绍
- **命令 /trs**

### 配置文件

```yml
# 服务器列表
server-info:
  - name: "测试服务器 #1"
    group: "测试服务器组"
    ip: "127.0.0.1"
    port: 19132

# 服务器信息更新方式 可选: Simple, Details
UpdateInfoProvide: "Simple"

# 在MOTD显示所有服务器的玩家数量总数
sync-player: false

#服务器关闭时传送到其他服务器
ServerCloseTransfer:
  enable: false
  showTitle:
    title: "服务器即将关闭!"
    subTitle: "您将会被转移到其他服务器!"
  ip: "127.0.0.1"
  port: 19132
  # 为 true 时将在 serverList 中寻找可用的服务器进行转移
  # 为 false 时则会转移到 ServerCloseTransfer.ip&prot 定义的服务器中
  TransferMode: false
  # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  serverList:
  - lobby1
  - lobby2
```
