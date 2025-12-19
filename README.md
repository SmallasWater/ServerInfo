# **ServerInfo**
[下载地址](https://motci.cn/job/ServerInfo/)  

- Nukkit服务器的跨服菜单
- 显示服务器的在线人数

## 使用介绍
- **命令: /trs**
- **在启用多服务器转移时建议看完注意事项**

### 配置文件

<details>
<summary>config.yml</summary>

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
  # 如果使用 WaterdogPE 则启用该功能
  use-WaterdogPE: false
  # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  ServerList:
  - lobby1
  - lobby2
```

</details>

### **注意事项**
- **给出是示例配置文件会省略部分无关的配置项，只会给出跟功能相关的配置项**
- **在下方会将 `WaterdogPE` 简写为 `WDPE`，仅有部分内容保留 `WaterdogPE` 的写法**

#### 一、**启用多服务器转移注意事项**
1. **`ServerCloseTransfer.ServerList` 配置的服务器名是在 `server-info` 中定义的，定义的名字为 `lobby1` 就填写 `lobby1`**

- **与 `WDPE` 搭配使用示例在下方给出**

<details>
<summary>ServerInfo config.yml</summary>

```yml
# 省略了部分无关的配置项

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
  enable: true
  TransferMode: true
 # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  ServerList:
  - lobby1
  - lobby2
```

</details>

#### 二、**启用多服务器转移并与 WDPE 搭配使用的注意事项**

1. **`server.properties` 要与本插件的配置文件中的 `server-info` 定义的ip端口一致，才能够正常的使用**
2. **特别是在与 `WDPE` 搭配使用时子服的 `server.properties` 要与本插件的配置文件中的 `server-info` 定义的ip端口必须一致才能够正常的使用，通常子服的ip是填写成 `127.0.0.1`**

- **示例在下方给出**

<details>
<summary>lobby1 server.properties</summary>

```properties
server-port=19201
server-ip=127.0.0.1
```

</details>

<details>
<summary>lobby2 server.properties</summary>

```properties
server-port=19202
server-ip=127.0.0.1
```

</details>

<details>
<summary>WDPE config.yml</summary>

```yml
# 省略了部分无关的配置项

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
  enable: true
  # 为 true 时将在 ServerList 中寻找可用的服务器进行转移
  # 为 false 时则会转移到 ServerCloseTransfer.ip&prot 定义的服务器中
  TransferMode: true
  # 如果使用 WaterdogPE 则启用该功能
  use-WaterdogPE: true
  # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  ServerList:
    - lobby1
    - lobby2
```

</details>

#### 三、启用多服务器转移但不搭配 WDPE 来使用****
1. **服务端的 `server-ip=` 为 `0.0.0.0` 即可**
2. **因为服务器的ip和port(端口)是判断可供转移的服务器是否为当前服务器的，所以是需要修改 `ServerCloseTransfer.ip` 的ip来实现以防玩家转移原服务器**

```yml
# 省略了部分无关的配置项

server-info:
  - name: "lobby1"
    group: "lobby"
    ip: "mc.example.com"
    port: 19201
  - name: "lobby2"
    group: "lobby"
    ip: "mc.example.com"
    port: 19202


#服务器关闭时传送到其他服务器
ServerCloseTransfer:
  enable: true
  ip: "mc.example.com"
  # 为 true 时将在 ServerList 中寻找可用的服务器进行转移
  # 为 false 时则会转移到 ServerCloseTransfer.ip&prot 定义的服务器中
  TransferMode: true
  # 如果使用 WaterdogPE 则启用该功能
  use-WaterdogPE: false
  # 可转移的服务器列表，名称为 server-info 中定义的 name
  # 例如：name 定义了 lobby1 就填写 lobby1
  ServerList:
    - lobby1
    - lobby2
```
