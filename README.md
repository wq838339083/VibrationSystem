# 安卓手机通讯震动系统

这是一个安卓手机通讯震动系统，包含发送端和接收端应用，通过PHP服务器实现多设备间的震动通知功能。

## 系统组成

系统由三个主要部分组成：

1. **发送端应用**：允许用户选择震动模式并发送到服务器
2. **接收端应用**：自动接收服务器消息并执行相应震动
3. **PHP服务器**：处理数据传输和存储

## 功能特点

- 支持三种震动模式：单次震动、两次震动、一长一短
- 一个发送端可以同时通知多个接收端
- 接收端自动轮询服务器获取最新震动指令
- 使用PHP和MySQL实现简单高效的服务器端

## 安装说明

### 服务器端配置

1. 将`server`目录下的文件上传到您的宝塔面板服务器
2. 在MySQL中执行`setup_database.sql`脚本创建数据库和表
3. 修改`vibration_server.php`中的数据库连接信息：
   ```php
   $db_host = 'localhost';
   $db_user = 'your_db_username'; // 替换为您的数据库用户名
   $db_pass = 'your_db_password'; // 替换为您的数据库密码
   $db_name = 'vibration_app';
   ```

### 安卓应用配置

1. 在Android Studio中打开`VibrationSender`和`VibrationReceiver`项目
2. 修改两个项目中的`MainActivity.java`文件，将`SERVER_URL`变量更新为您的服务器地址：
   ```java
   private static final String SERVER_URL = "http://您的服务器IP或域名/vibration_server.php?action=send";
   ```
   ```java
   private static final String SERVER_URL = "http://您的服务器IP或域名/vibration_server.php?action=receive";
   ```
3. 编译并安装应用到安卓设备上

## 使用方法

### 发送端

1. 打开震动发送器应用
2. 选择一种震动模式（单次震动、两次震动或一长一短）
3. 点击"发送震动"按钮

### 接收端

1. 打开震动接收器应用
2. 应用会自动连接服务器并等待震动指令
3. 收到指令后会自动执行相应的震动模式

## 系统要求

- 安卓设备：Android 5.0 (API 21) 或更高版本
- 服务器：支持PHP 7.0+和MySQL 5.6+的宝塔面板服务器
- 网络：发送端和接收端设备需要能够访问互联网

## 注意事项

- 确保服务器的PHP配置允许跨域请求
- 接收端应用会定期轮询服务器，可能会消耗一定的网络流量和电池
- 为了安全起见，在实际部署时应添加适当的身份验证机制