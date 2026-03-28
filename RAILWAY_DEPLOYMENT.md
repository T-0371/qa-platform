# QA Platform Railway 部署详细指南

## 目录
1. [部署架构概述](#部署架构概述)
2. [准备工作](#准备工作)
3. [代码准备](#代码准备)
4. [Railway部署步骤](#railway部署步骤)
5. [数据库配置](#数据库配置)
6. [域名绑定](#域名绑定)
7. [部署验证](#部署验证)
8. [常见问题解决](#常见问题解决)

---

## 部署架构概述

### 系统架构
```
┌─────────────────────────────────────────────────────────────────┐
│                        用户浏览器                                 │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Railway 平台                                │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │              QA Platform 应用                               ││
│  │              (Spring Boot JAR)                              ││
│  │              端口: 8080                                     ││
│  └─────────────────────────────────────────────────────────────┘│
│                                │                                 │
│                                ▼                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │              MySQL 数据库                                    ││
│  │              (Railway PostgreSQL/MySQL)                     ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
                        your-app.railway.app
```

### 费用说明
- **Railway 免费额度**：
  - 500小时/月运行时间
  - 100GB出站流量/月
  - 1GB RAM / 0.5 vCPU
  - 足够个人项目使用

---

## 准备工作

### 1.1 准备GitHub仓库

#### 如果还没有GitHub仓库，执行以下步骤：

**步骤1：初始化本地Git仓库**
```bash
# 进入项目目录
cd E:\Test\XiangMu\LunTan_Demo\qa-platform

# 初始化Git仓库
git init

# 添加所有文件
git add .

# 提交代码
git commit -m "Initial commit - QA Platform"
```

**步骤2：创建GitHub仓库**
1. 登录 GitHub：https://github.com
2. 点击右上角 **+** → **New repository**
3. 填写仓库信息：
   - **Repository name**: `qa-platform`
   - **Description**: `IT技术问答平台`
   - **Visibility**: Private（私有）或 Public（公开）
   - **不要勾选** "Add a README file"（我们已有代码）
4. 点击 **Create repository**

**步骤3：关联本地仓库到GitHub**
```bash
# 添加远程仓库地址（替换为您的实际地址）
git remote add origin https://github.com/您的GitHub用户名/qa-platform.git

# 重命名分支为main（如果是master）
git branch -M main

# 推送代码到GitHub
git push -u origin main
```

**步骤4：验证推送成功**
1. 刷新GitHub仓库页面
2. 确认所有文件已上传

---

## 代码准备

### 2.1 创建生产环境配置文件

已创建 `src/main/resources/application-prod.yml`，包含：
- 环境变量配置（数据库连接等）
- 生产环境优化设置
- 日志配置

### 2.2 修改现有配置文件

确保 `src/main/resources/application.yml` 不包含敏感信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/qa_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    # ... 其他配置保持不变
```

> 注意：生产环境配置已移至 `application-prod.yml`，本地开发仍使用 `application.yml`

### 2.3 添加必要的系统属性配置

在 `application-prod.yml` 中已添加：
```yaml
server:
  port: ${PORT:8080}
  address: 0.0.0.0
```

### 2.4 上传修改后的代码到GitHub

```bash
# 添加修改的文件
git add .

# 提交
git commit -m "Add production config for Railway deployment"

# 推送到GitHub
git push origin main
```

---

## Railway部署步骤

### 3.1 注册Railway账号

**步骤1：访问Railway官网**
1. 打开浏览器访问：https://railway.app
2. 点击 **Sign Up** 注册

**步骤2：选择登录方式**
- **推荐**：使用 GitHub 账号登录（方便后续关联仓库）
- 也可以使用邮箱注册

**步骤3：完成身份验证**
1. 验证邮箱（如果使用邮箱注册）
2. 完成初步设置

### 3.2 创建新项目

**步骤1：创建新项目**
1. 登录 Railway 控制台：https://railway.app/dashboard
2. 点击 **New Project**
3. 选择 **Deploy from GitHub repo**

**步骤2：连接GitHub仓库**
1. 首次使用需要授权 GitHub 访问
2. 在搜索框中输入 `qa-platform`
3. 选择您的仓库

**步骤3：选择仓库分支**
1. 默认选择 `main` 分支
2. 点击 **Deploy Now**

### 3.3 配置项目设置

Railway会自动检测为Maven项目并尝试构建。如果自动配置失败，按以下步骤手动配置：

**步骤1：进入项目设置**
1. 点击刚创建的项目
2. 点击 **Settings** 标签

**步骤2：配置构建设置**
找到 **Build** 部分：
```
构建命令：mvn clean package -DskipTests
输出目录：target/qa-platform-1.0-SNAPSHOT.jar
```

找到 **Start** 部分：
```
启动命令：java -jar target/qa-platform-1.0-SNAPSHOT.jar
```

或者更简单的配置：
```
构建命令：mvn clean package -DskipTests
启动命令：java -Dserver.port=$PORT -jar target/qa-platform-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**步骤3：配置环境变量**
1. 点击 **Variables** 标签
2. 添加以下环境变量：

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `PORT` | `8080` | Railway分配端口 |
| `DB_HOST` | `容器内主机名` | MySQL主机地址 |
| `DB_PORT` | `3306` | MySQL端口 |
| `DB_NAME` | `railway` | 数据库名 |
| `DB_USERNAME` | `root` | 数据库用户名 |
| `DB_PASSWORD` | `您的密码` | 数据库密码 |

### 3.4 创建MySQL数据库

**步骤1：在Railway中添加MySQL插件**
1. 进入您的项目页面
2. 点击 **New** → **Database** → **Add MySQL**

**步骤2：获取数据库连接信息**
1. 点击 **MySQL** 数据库
2. 点击 **Connect** 标签
3. 记录以下信息：
   - **Host**: `containers-us-west-xxx.railway.app`
   - **Port**: `xxxxx`
   - **Database**: `railway`
   - **Username**: `root`
   - **Password**: `xxxxxxxxxxxxxx`

**步骤3：更新环境变量**
回到项目设置，填写实际的数据库连接信息：
- `DB_HOST`: 您的MySQL主机地址
- `DB_PORT`: 您的MySQL端口
- `DB_NAME`: `railway`
- `DB_USERNAME`: `root`
- `DB_PASSWORD`: 您的MySQL密码

### 3.5 配置数据库表结构

Railway的MySQL创建后是空的，需要导入数据库表结构。

**方法1：使用Railway的MySQL客户端**

1. 在Railway控制台中点击MySQL数据库
2. 点击 **Playground** 标签
3. 这是一个在线MySQL客户端
4. 复制 `src/main/resources/qa_platform.sql` 的内容
5. 粘贴到Playground中执行

**方法2：使用本地MySQL客户端连接**

```bash
# 安装MySQL客户端（如果还没有）
# Windows: 下载 MySQL Workbench
# macOS: brew install mysql-client

# 连接数据库
mysql -h containers-us-west-xxx.railway.app -P xxxxx -u root -p

# 选择数据库
USE railway;

# 导入SQL文件（需要在服务器上先下载sql文件或通过其他方式传输）
source qa_platform.sql;
```

**方法3：修改项目支持自动创建表**

在 `application-prod.yml` 中添加：
```yaml
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
```

> 注意：需要创建 `schema.sql` 和 `data.sql` 文件

---

## 域名绑定

### 4.1 配置自定义域名（可选）

**步骤1：获取Railway分配的域名**
1. 在项目中点击 **Settings**
2. 找到 **Networking** 部分
3. 复制 **Public Domain**，格式如：
   ```
   qa-platform.railway.app
   ```

**步骤2：绑定自定义域名**
1. 进入项目 **Settings** → **Networking**
2. 点击 **Generate Domain**
3. 输入您的域名（如 `qa.yourdomain.com`）
4. 按提示在您的DNS服务商处添加CNAME记录

**步骤3：配置DNS**
在你的域名提供商（如阿里云、腾讯云、Cloudflare等）添加：
```
类型: CNAME
名称: qa（或您选择的子域名）
值: qa-platform.railway.app
TTL: 3600（或自动）
```

### 4.2 配置SSL证书

Railway会自动为所有域名配置SSL证书，无需手动配置。

---

## 部署验证

### 5.1 检查部署状态

**步骤1：查看部署日志**
1. 在Railway控制台中点击您的项目
2. 点击 **Deployments** 标签
3. 点击最新的部署记录
4. 查看构建和运行日志

**步骤2：常见日志问题及解决**

| 问题 | 可能原因 | 解决方法 |
|------|---------|---------|
| 构建失败 | Maven依赖下载失败 | 检查网络，重试部署 |
| 启动失败 | 端口被占用 | 确保 `$PORT` 环境变量正确 |
| 数据库连接失败 | 环境变量配置错误 | 检查DB_HOST等变量 |
| 内存溢出 | JVM内存配置过大 | 减少 `-Xmx` 参数值 |

### 5.2 访问应用

**步骤1：使用Railway分配的域名访问**
```
https://qa-platform.railway.app
```

**步骤2：验证功能**
1. ✅ 访问首页
2. ✅ 测试登录/注册
3. ✅ 测试发布问题
4. ✅ 测试聊天功能
5. ✅ 测试文件上传

### 5.3 查看日志

**步骤1：查看应用日志**
1. 在Railway控制台中点击项目
2. 点击 **Logs** 标签
3. 查看实时日志输出

**步骤2：常用日志命令**
Railway提供的基础日志功能：
- 实时日志流
- 历史日志（最近1000行）

---

## 常见问题解决

### 6.1 构建问题

**问题1：Maven依赖下载超时**
```
解决：
1. 检查网络连接
2. 重试部署
3. 或添加Maven镜像仓库配置
```

在项目根目录添加 `settings.xml` 并在构建命令中使用：
```bash
mvn clean package -DskipTests -s settings.xml
```

**问题2：Java版本不兼容**
Railway默认使用OpenJDK 11，如果需要其他版本：

在 `system.properties` 文件中指定：
```
java.runtime.version=17
```

### 6.2 启动问题

**问题1：端口绑定失败**
```
Error: Could not bind to port
原因：应用尝试使用固定端口而非$PORT环境变量
解决：确保启动命令使用 $PORT
```

启动命令改为：
```bash
java -Dserver.port=$PORT -jar target/qa-platform-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**问题2：数据库连接失败**
```
Error: Unable to connect to database
原因：数据库地址或凭证错误
解决：
1. 检查DB_HOST, DB_PORT等环境变量
2. 确认MySQL数据库已启动
3. 检查数据库用户权限
```

### 6.3 数据库问题

**问题1：数据库表不存在**
```
解决：
1. 手动导入SQL文件
2. 或启用spring.sql.init.mode=always自动创建
```

**问题2：数据库迁移**
如果需要修改表结构：
1. 备份现有数据
2. 修改SQL文件
3. 重新导入
4. 或使用Flyway/Liquibase管理数据库版本

### 6.4 性能问题

**问题1：响应速度慢**
```
优化建议：
1. 启用Redis缓存（Railway提供Redis插件）
2. 优化数据库查询
3. 启用Gzip压缩（已在配置中启用）
4. 添加CDN加速静态资源
```

**问题2：内存不足**
```
解决：
1. 减少JVM堆内存配置
2. 启动命令改为：java -Xmx256m -jar target/xxx.jar
```

### 6.5 费用问题

**问题1：免费额度用完**
```
Railway免费额度：
- 500小时/月运行时间
- 超出后按量付费

避免超额建议：
1. 不使用时停止项目
2. 设置预算告警
3. 考虑其他免费平台
```

**问题2：如何停止项目**
```
在Railway控制台：
1. 进入项目设置
2. 点击 Pause Deployment
3. 暂停后不会产生运行时间费用
```

---

## 高级配置

### 7.1 配置Redis缓存（可选）

**步骤1：添加Redis插件**
1. 在项目中点击 **New** → **Database** → **Add Redis**

**步骤2：获取Redis连接信息**
1. 点击Redis数据库
2. 记录连接信息

**步骤3：添加Redis配置**
在环境变量中添加：
```
REDIS_HOST=您的Redis主机
REDIS_PORT=6379
REDIS_PASSWORD=您的密码
```

在 `application-prod.yml` 中添加：
```yaml
spring:
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
```

### 7.2 配置对象存储（可选）

Railway不提供对象存储，可以使用：
- **Cloudflare R2**（免费额度大）
- **AWS S3**（免费1GB）
- **Backblaze B2**（免费10GB）

### 7.3 配置CI/CD自动部署

**步骤1：在GitHub中配置**
1. 进入GitHub仓库 **Settings**
2. 点击 **Branches**
3. 添加保护规则（如需要）

**步骤2：Railway自动部署**
Railway默认会在每次push到main分支时自动重新部署。

如需关闭：
1. 在Railway项目设置中
2. 找到 **Git Settings**
3. 关闭 **Automatic Deployments**

### 7.4 配置环境特定的构建

可以在GitHub仓库创建不同分支用于不同环境：
- `main` → 生产环境
- `dev` → 开发环境

在Railway中分别为每个分支创建项目。

---

## 最佳实践

### 8.1 安全性建议

1. **不要在代码中硬编码敏感信息**
   - 所有敏感信息通过环境变量注入
   - 使用 Railway 的 Secrets 功能

2. **启用HTTPS**
   - Railway自动为所有域名提供SSL
   - 确保强制使用HTTPS

3. **定期更新依赖**
   - 检查并更新Maven依赖版本
   - 关注安全漏洞公告

### 8.2 监控建议

1. **设置预算告警**
   - 在Railway中设置月度预算
   - 避免意外超支

2. **监控应用状态**
   - 使用Railway的健康检查
   - 配置自动重启策略

3. **日志管理**
   - 定期检查错误日志
   - 设置关键错误的告警通知

### 8.3 备份建议

1. **数据库定期备份**
   - Railway提供数据库备份功能
   - 建议每周手动备份一次

2. **重要数据导出**
   - 定期导出重要业务数据
   - 保存到本地或其他云存储

---

## 快速参考

### 构建命令
```bash
mvn clean package -DskipTests
```

### 启动命令
```bash
java -Dserver.port=$PORT -jar target/qa-platform-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 必要环境变量
```
PORT=8080
DB_HOST=containers-us-west-xxx.railway.app
DB_PORT=xxxxx
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=xxxxxxxxxxxxxx
```

### 访问地址
```
生产环境：https://qa-platform.railway.app
本地环境：http://localhost:8080
```

---

## 联系支持

- **Railway文档**：https://docs.railway.app
- **Railway Discord**：https://discord.gg/railway
- **GitHub Issues**：https://github.com/railwayapp/railway/issues

---

**文档版本**：1.0
**最后更新**：2026-03-21
**适用项目**：QA Platform (Spring Boot + MySQL)
**部署平台**：Railway