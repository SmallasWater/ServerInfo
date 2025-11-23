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
  # 为 true 时将在 ServerList 中寻找可用的服务器进行转移
  # 为 false 时则会转移到 ServerCloseTransfer.ip&prot 定义的服务器中
  TransferMode: false
  # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  ServerList:
  - lobby1
  - lobby2
```

### **注意事项**

#### 一、

- **`server.properties` 要与本插件的配置文件中的 `server-info` 定义的ip端口一致，才能够正常的使用**
- **特别是在与 `WaterdogPE` 搭配使用时子服的 `server.properties` 要与本插件的配置文件中的 `server-info` 定义的ip端口必须一致才能够正常的使用，通常子服的ip是填写成 `127.0.0.1`**
- **与 `WaterdogPE` 搭配使用示例在下方给出**

**lobby1**
```properties
server-port=19201
server-ip=127.0.0.1
```
**lobby2**
```properties
server-port=19202
server-ip=127.0.0.1
```
**config.yml**
```yml
server-info:
  - name: "lobby1"
    group: "lobby"
    ip: "127.0.0.1"
    port: 19201
  - name: "lobby2"
    group: "lobby"
    ip: "127.0.0.1"
    port: 19202
```

#### 二、
- **`ServerCloseTransfer.ServerList` 配置的服务器名是在 `server-info` 中定义的，定义的名字为 `lobby1` 就填写 `lobby1`**

```yml

server-info:
  - name: "lobby1"
    group: "lobby"
    ip: "127.0.0.1"
    port: 19201
  - name: "lobby2"
    group: "lobby"
    ip: "127.0.0.1"
    port: 19202

#服务器关闭时传送到其他服务器
ServerCloseTransfer:
  enable: false
  showTitle:
    title: "服务器即将关闭!"
    subTitle: "您将会被转移到其他服务器!"
  ip: "127.0.0.1"
  port: 19132
  # 为 true 时将在 ServerList 中寻找可用的服务器进行转移
  # 为 false 时则会转移到 ServerCloseTransfer.ip&prot 定义的服务器中
  TransferMode: true
  # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  ServerList:
  - lobby1
  - lobby2
```