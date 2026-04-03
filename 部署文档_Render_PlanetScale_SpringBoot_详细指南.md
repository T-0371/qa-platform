# Render + Railway MySQL 部署 Spring Boot 项目详细指南

版本：v3.0
适用人群：个人开发者、学生、初创团队
目标：通过 Render + Railway MySQL 方案稳定部署 Spring Boot + MySQL 项目
方案优势：Render 免费套餐无到期时间，Railway MySQL 提供稳定的数据库服务

---

## 📌 目录

1. [方案优势分析](#1-方案优势分析)
2. [预备工作](#2-预备工作)
3. [Render 平台注册与配置](#3-render-平台注册与配置)
4. [Railway MySQL 数据库创建](#4-railway-mysql-数据库创建)
5. [Spring Boot 项目配置](#5-spring-boot-项目配置)
6. [Render 部署配置](#6-render-部署配置)
7. [环境变量配置详解](#7-环境变量配置详解)
8. [部署验证与测试](#8-部署验证与测试)
9. [常见问题排查](#9-常见问题排查)
10. [维护与监控](#10-维护与监控)

---

## 1. 方案优势分析

### 1.1 为什么选择 Render + Railway MySQL？

| 对比项 | Railway 全部服务 | Render + Railway MySQL | 说明 |
|--------|----------------|------------------------|------|
| 免费时长 | 有限 | 无限制 | Render 免费套餐永不过期 |
| 内存限制 | 512MB | 512MB | 应用内存占用相同 |
| 数据库 | 共享内存 | 独立服务 | Railway MySQL 独立部署，不占用应用内存 |
| 稳定性 | 一般 | 优秀 | 应用和数据库分离，互不影响 |
| 配置难度 | 复杂 | 简单 | Render 配置更直观 |
| 免费额度 | 有限 | 充足 | Railway MySQL 提供合理的免费额度 |

### 1.2 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                    用户访问                                      │
│                https://qa-platform.onrender.com                 │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Render 平台                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │           Spring Boot 应用（Web Service）                │   │
│  │  · 接收 HTTP 请求                                        │   │
│  │  · 处理业务逻辑                                          │   │
│  │  · JVM 内存：256MB                                      │   │
│  │  · 静态资源：/app/uploads/                              │   │
│  └─────────────────────────────────────────────────────────┘   │
│  实例类型：Free (0.5 CPU, 512MB RAM)                           │
│  特性：自动休眠（冷启动约30-50秒）                              │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    │ JDBC连接（加密）
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Railway 平台                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │               MySQL 数据库                              │   │
│  │  · 数据持久化存储                                       │   │
│  │  · 独立部署，不占用应用内存                               │   │
│  │  · 内置备份                                             │   │
│  └─────────────────────────────────────────────────────────┘   │
│  存储空间：1GB（免费版）                                        │
│  连接数：最大 100 个                                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 预备工作

### 2.1 所需账户

| 平台 | 账户类型 | 注册地址 | 备注 |
|------|---------|---------|------|
| GitHub | 必须 | https://github.com | 代码仓库 |
| Render | 必须 | https://render.com | 应用部署 |
| Railway | 必须 | https://railway.app | 数据库服务 |

### 2.2 注册 GitHub（如果尚未注册）

1. 访问 https://github.com
2. 点击 "Sign Up"
3. 填写用户名、邮箱、密码
4. 验证邮箱完成注册
5. 创建新仓库 `qa-platform`

### 2.3 推送代码到 GitHub

```bash
# 进入项目目录
cd qa-platform

# 初始化 Git（如果尚未初始化）
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: Spring Boot + MySQL project"

# 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/qa-platform.git

# 推送代码
git push -u origin main
```

### 2.4 预备检查清单

- [ ] GitHub 账户已注册
- [ ] GitHub 仓库已创建
- [ ] 项目代码已推送到 GitHub
- [ ] GitHub 仓库设为 Public（Render 免费版需要 Public 仓库）

---

## 3. Render 平台注册与配置

### 3.1 注册 Render 账户

1. **访问 Render 官网**
   ```
   https://render.com
   ```

2. **点击 "Get Started" 或 "Sign Up"**

3. **选择注册方式**
   - 推荐：使用 GitHub 账号登录（最便捷）
   - 也可使用 Google 账号或邮箱注册

4. **授权 GitHub 访问**
   - 如果使用 GitHub 登录，需要授权 Render 访问 GitHub
   - 选择要授权的仓库范围：
     - All repositories（所有仓库）
     - Only select repositories（仅选择特定仓库）
   - 建议选择 "Only select repositories" 并选择 `qa-platform`

5. **完成账户创建**
   - 设置组织名称（可选）
   - 完成初始设置向导

### 3.2 Render 控制台概览

登录后的控制台主要区域：

```
┌─────────────────────────────────────────────────────────────────┐
│  Render Dashboard                                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  🏠 Dashboard   📊 Billing   🔑 API Keys   ⚙️ Settings         │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Your Services                                            │   │
│  │                                                          │   │
│  │  + New +                                                 │   │
│  │                                                          │   │
│  │  ┌─────────────────────────────────────────────────────┐ │   │
│  │  │ Web Services          Status: Running 🟢            │ │   │
│  │  │ qa-platform           https://qa-platform.onrender  │ │   │
│  │  └─────────────────────────────────────────────────────┘ │   │
│  │                                                          │   │
│  │  ┌─────────────────────────────────────────────────────┐ │   │
│  │  │ Private Services     Status: -                      │ │   │
│  │  └─────────────────────────────────────────────────────┘ │   │
│  │                                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Railway MySQL 数据库创建

### 4.1 注册 Railway 账户

1. **访问 Railway 官网**
   ```
   https://railway.app
   ```

2. **点击 "Sign Up"**

3. **选择登录方式**
   - 推荐：使用 GitHub 账号授权登录
   - 也可使用邮箱注册

4. **完成邮箱验证**（如需要）

5. **进入 Railway 控制台**

### 4.2 创建新数据库

#### 4.2.1 创建步骤

1. **登录 Railway 控制台**
   ```
   https://railway.app/dashboard
   ```

2. **创建新项目**
   - 点击 "New Project"
   - 选择 "Empty Project"

3. **添加 MySQL 数据库**
   - 在项目页面点击 "Add Service"
   - 选择 "Database"
   - 选择 "MySQL"
   - 等待数据库创建完成

4. **数据库配置**
   - 数据库名称会自动生成（如：`mysql-railway-12345`）
   - 系统会自动分配资源

#### 4.2.2 数据库信息示例

```
┌─────────────────────────────────────────────────────────────────┐
│  Service: MySQL                                              🟢 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Type: MySQL Database                                          │
│  Status: Ready                                                 │
│  Region: us-east1                                              │
│  Created: 2026-04-03                                            │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Connection Info                                        │   │
│  │                                                          │   │
│  │  Host:    containers-us-west-123.railway.app            │   │
│  │  Port:    12345                                         │   │
│  │  Database: railway                                      │   │
│  │  Username: root                                          │   │
│  │  Password: [已隐藏]                          [Copy]     │   │
│  │                                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  [Connect]  [Variables]  [Metrics]  [Settings]               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 获取数据库连接凭证

1. **进入数据库服务详情页**

2. **查看连接信息**
   - 点击 "Variables" 标签
   - 系统会显示所有环境变量

3. **记录以下信息**（稍后需要用到）

```
DB_HOST = containers-us-west-123.railway.app
DB_PORT = 12345
DB_NAME = railway
DB_USERNAME = root
DB_PASSWORD = xxxxxxxxxxxxxxxxxxxxxxx
```

### 4.4 配置数据库连接（重要）

Railway MySQL 不需要强制 SSL 连接。配置 JDBC URL 格式：

**JDBC URL（用于 application-prod.yml）**：
```
jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&connectTimeout=20000&socketTimeout=30000
```

**示例**：
```
jdbc:mysql://containers-us-west-123.railway.app:12345/railway?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&connectTimeout=20000&socketTimeout=30000
```

---

## 5. Spring Boot 项目配置

### 5.1 项目结构

确保项目包含以下关键文件：

```
qa-platform/
├── src/main/
│   ├── java/com/example/qa/
│   │   ├── QaPlatformApplication.java
│   │   ├── controller/
│   │   │   └── HealthController.java
│   │   └── ...
│   └── resources/
│       ├── application-prod.yml    ← 生产环境配置
│       ├── application-dev.yml     ← 开发环境配置
│       ├── qa_platform.sql         ← 主数据库表结构
│       ├── message_table.sql       ← 消息表结构
│       └── system_config.sql       ← 系统配置表
├── pom.xml
├── railway.json                    ← 可保留但 Render 不使用
└── Dockerfile                      ← 可选，Render 会自动检测
```

### 5.2 application-prod.yml 配置（关键）

这是最重要的配置文件，必须使用环境变量占位符：

```yaml
spring:
  datasource:
    # 使用环境变量占位符，运行时由 Render 注入真实值
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&connectTimeout=20000&socketTimeout=30000
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 1
      maximum-pool-size: 3
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 20000
      pool-name: QAHikariCP
      connection-test-query: SELECT 1
      leak-detection-threshold: 60000

  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB

  web:
    resources:
      static-locations: classpath:/static/,file:/app/uploads/
      cache:
        period: 86400

  jmx:
    enabled: false

  sql:
    init:
      mode: never

  jpa:
    hibernate:
      ddl-auto: none
    defer-datasource-initialization: true
    open-in-view: false

mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.example.qa.entity
  configuration:
    cache-enabled: true
    lazy-loading-enabled: true
    default-statement-timeout: 3000
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
    banner: false

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html

server:
  port: ${PORT:8080}
  address: 0.0.0.0
  compression:
    enabled: false

management:
  enabled: false

file:
  upload:
    path: /app/uploads/

logging:
  level:
    root: INFO
    com.example.qa: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### 5.3 pom.xml 关键依赖

确保包含以下依赖：

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.2</version>
</dependency>

<!-- MySQL 驱动（注意：使用 mysql-connector-j） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
    <scope>runtime</scope>
</dependency>

<!-- Spring Boot Maven 插件 -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

### 5.4 健康检查端点

确保项目包含健康检查控制器（HealthController.java）：

```java
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ApiResponse health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "QA Platform");
        health.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(health);
    }
}
```

### 5.5 提交代码到 GitHub

确保所有配置修改后，推送到 GitHub：

```bash
git add .
git commit -m "配置生产环境：使用环境变量连接Railway MySQL数据库"
git push origin main
```

---

## 6. Render 部署配置

### 6.1 创建 Web Service

1. **登录 Render 控制台**
   ```
   https://dashboard.render.com
   ```

2. **点击 "New +" 按钮**

3. **选择 "Web Service"**

4. **连接 GitHub 仓库**
   - 如果首次使用，需要安装 Render GitHub App
   - 选择 `qa-platform` 仓库
   - 选择分支（通常为 `main`）

### 6.2 配置 Web Service

#### 6.2.1 基本信息配置

| 参数 | 配置值 | 说明 |
|------|--------|------|
| **Source Code** | `T-0371/qa-platform` | 选择您的 GitHub 仓库 |
| **Name** | `qa-platform` | 服务名称，用于生成 URL |
| **Language** | `Node` | 选择 Node（Render 会自动检测 Java） |
| **Branch** | `main` | 要构建的分支 |
| **Region** | `Singapore (Southeast Asia)` | 选择离您最近的区域 |
| **Root Directory** | 留空 | 使用默认根目录 |

#### 6.2.2 构建和启动配置

| 参数 | 配置值 | 说明 |
|------|--------|------|
| **Build Command** | `mvn clean package -DskipTests` | Maven 构建命令，跳过测试以加快构建速度 |
| **Start Command** | `java -Xmx256m -Xms64m -XX:MaxMetaspaceSize=128m -jar target/qa-platform.jar --spring.profiles.active=prod` | 启动命令，包含 JVM 内存优化参数 |
| **Instance Type** | `Free` | 选择免费实例（512MB RAM, 0.1CPU） |

#### 6.2.3 环境变量配置

在 **Environment Variables** 部分添加以下 6 个变量：

| 变量名 | 示例值 | 说明 |
|--------|--------|------|
| **SPRING_PROFILES_ACTIVE** | `prod` | 激活生产环境配置 |
| **DB_HOST** | `containers-us-west-123.railway.app` | Railway MySQL 主机名 |
| **DB_PORT** | `12345` | MySQL 端口 |
| **DB_NAME** | `railway` | 数据库名称 |
| **DB_USERNAME** | `root` | 数据库用户名 |
| **DB_PASSWORD** | `xxxxxxxx` | 数据库密码（从 Railway 获取） |

#### 6.2.4 高级配置

| 参数 | 配置值 | 说明 |
|------|--------|------|
| **Health Check Path** | `/health` | 健康检查端点路径 |
| **Pre-Deploy Command** | 留空 | 部署前命令（如需数据库迁移可在此设置） |
| **Auto-Deploy** | `On Commit` | 代码提交时自动部署 |
| **Build Filters** | 留空 | 构建过滤器（默认即可） |

**对于需要特定语言的构建**：

如果 Render 无法自动检测 Java，可以创建一个 `render.yaml` 文件：

```yaml
services:
  - type: web
    name: qa-platform
    env: docker
    dockerfilePath: ./Dockerfile
    healthCheckPath: /health
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
```

### 6.3 环境变量配置（关键）

在 Render 控制台的环境变量部分，添加以下变量：

| 变量名 | 值（示例） | 说明 |
|--------|-----------|------|
| SPRING_PROFILES_ACTIVE | prod | 激活生产环境配置 |
| DB_HOST | containers-us-west-123.railway.app | Railway MySQL 主机名 |
| DB_PORT | 12345 | MySQL 端口 |
| DB_NAME | railway | 数据库名称 |
| DB_USERNAME | root | 数据库用户名 |
| DB_PASSWORD | xxxxxxxx | 数据库密码 |
| PORT | 10000 | Render 分配的端口 |

**⚠️ 重要说明**：
- Render 的免费套餐会自动分配端口，通常是 10000
- PORT 环境变量由 Render 自动设置，不需要手动配置
- 数据库密码从 Railway 获取

### 6.4 启动命令

在 "Start Command" 字段输入：

```bash
java -Xmx256m -Xms64m -XX:MaxMetaspaceSize=128m -jar target/qa-platform.jar --spring.profiles.active=prod
```

**JVM 参数说明**：
- `-Xmx256m`：最大堆内存 256MB
- `-Xms64m`：初始堆内存 64MB
- `-XX:MaxMetaspaceSize=128m`：最大元空间 128MB
- 总内存约 384-448MB，在 512MB 限制内

### 6.5 高级配置（可选）

#### 6.5.1 环境变量分组

如果需要分别配置多个环境：

```
Environment: (选择 Environment Variables 或 All Environments)
```

#### 6.5.2 磁盘挂载（用于文件上传）

如果应用需要持久化文件存储：

```
Mount Path: /app/uploads
```

### 6.6 部署操作步骤

1. **准备工作**：
   - 确保 GitHub 仓库已创建并包含完整的 Spring Boot 项目
   - 确保 `application-prod.yml` 配置正确（使用环境变量占位符）
   - 确保 HealthController 已实现

2. **创建 Web Service**：
   - 登录 Render 控制台
   - 点击 "New +" → "Web Service"
   - 选择您的 GitHub 仓库 `T-0371/qa-platform`

3. **填写配置**：
   - **基本信息**：
     - Name: `qa-platform`
     - Language: `Node`（Render 会自动检测 Java）
     - Branch: `main`
     - Region: `Singapore (Southeast Asia)`
   - **构建配置**：
     - Build Command: `mvn clean package -DskipTests`
     - Start Command: `java -Xmx256m -Xms64m -XX:MaxMetaspaceSize=128m -jar target/qa-platform.jar --spring.profiles.active=prod`
     - Instance Type: `Free`
   - **环境变量**：
     - 点击 "Add Environment Variable"
     - 逐一添加 6 个环境变量
     - 数据库连接信息从 Railway 获取
   - **高级配置**：
     - Health Check Path: `/health`
     - Auto-Deploy: `On Commit`

4. **完成部署**：
   - 检查所有配置是否正确
   - 点击 "Deploy Web Service" 按钮
   - 等待构建和部署完成（首次部署约 3-10 分钟）

5. **验证部署**：
   - 查看部署状态：应显示 "Live 🟢"
   - 访问健康检查端点：`https://qa-platform.onrender.com/health`
   - 访问应用首页：`https://qa-platform.onrender.com`
   - 查看构建日志：确保没有错误

### 6.7 构建命令详解
```bash
mvn clean package -DskipTests
```
- `mvn clean`：清理之前的构建产物
- `mvn package`：打包项目为 JAR 文件
- `-DskipTests`：跳过测试，加快构建速度

### 6.8 启动命令详解
```bash
java -Xmx256m -Xms64m -XX:MaxMetaspaceSize=128m -jar target/qa-platform.jar --spring.profiles.active=prod
```
- `-Xmx256m`：最大堆内存 256MB
- `-Xms64m`：初始堆内存 64MB
- `-XX:MaxMetaspaceSize=128m`：最大元空间 128MB
- `--spring.profiles.active=prod`：激活生产环境配置

---

## 7. 环境变量配置详解

### 7.1 为什么需要环境变量？

1. **安全性**：密码等敏感信息不硬编码在代码中
2. **灵活性**：同一套代码可用于多个环境
3. **平台集成**：云平台自动注入配置
4. **版本控制**：代码仓库不包含敏感信息

### 7.2 Render 环境变量

#### 必需的环境变量

| 变量名 | 示例值 | 说明 |
|--------|--------|------|
| SPRING_PROFILES_ACTIVE | prod | 激活生产环境配置 |
| DB_HOST | containers-us-west-123.railway.app | Railway MySQL 主机名 |
| DB_PORT | 12345 | MySQL 端口 |
| DB_NAME | railway | 数据库名称 |
| DB_USERNAME | root | 数据库用户名 |
| DB_PASSWORD | xxxxx | 数据库密码 |

#### Render 自动设置的变量

| 变量名 | 说明 | 可否修改 |
|--------|------|---------|
| PORT | 分配的端口（通常 10000） | ❌ 自动设置 |
| RAILWAY_STATIC_URL | 静态文件 URL | ❌ 自动设置 |

### 7.3 环境变量添加步骤

1. 进入 Web Service 详情页
2. 点击 "Environment" 标签
3. 在 "Environment Variables" 部分
4. 点击 "Add Environment Variable"
5. 逐一添加变量
6. 点击 "Save Changes"

### 7.4 本地开发环境配置

创建 `application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/qa_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_local_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 8. 部署验证与测试

### 8.1 部署状态检查

#### 8.1.1 查看部署日志

1. 进入 Web Service 详情页
2. 点击 "Logs" 标签
3. 查看构建和运行日志

**成功的日志示例**：
```
[2026-04-03 12:30:45] ==> Starting service with:
[2026-04-03 12:30:45] ==> java -Xmx256m -Xms64m -jar target/qa-platform.jar --spring.profiles.active=prod
[2026-04-03 12:31:02] ==> Spring Boot application started successfully
[2026-04-03 12:31:02] ==> Application is running at: https://qa-platform.onrender.com
```

**检查关键字**：
- ✅ `Started Application` - 启动成功
- ✅ `HikariPool - Start connection` - 数据库连接成功
- ❌ `OutOfMemoryError` - 内存不足
- ❌ `Connection refused` - 连接被拒绝
- ❌ `Access denied` - 认证失败

#### 8.1.2 查看应用状态

```
Status: Live 🟢
Uptime: 2 hours
Region: Singapore
Deployed: Apr 3, 2026, 12:30 PM
```

### 8.2 功能测试

#### 8.2.1 健康检查

```bash
# 使用 curl 测试
curl https://qa-platform.onrender.com/health

# 预期响应
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "UP",
    "application": "QA Platform",
    "timestamp": 1743667200000
  }
}
```

#### 8.2.2 首页访问

```
浏览器访问: https://qa-platform.onrender.com
```

预期：显示应用首页或登录页面

#### 8.2.3 API 接口测试

```bash
# 测试登录接口（示例）
curl -X POST https://qa-platform.onrender.com/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

### 8.3 数据库连接验证

在 Render 控制台执行以下操作：

1. 点击 "Shell" 打开命令行
2. 连接 Railway MySQL 数据库：

```bash
mysql -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD -D $DB_NAME
```

3. 执行验证查询：

```sql
-- 检查数据库版本
SELECT VERSION();

-- 检查当前数据库
SELECT DATABASE();

-- 检查表列表
SHOW TABLES;

-- 检查用户表
DESCRIBE user;
SELECT COUNT(*) FROM user;
```

---

## 9. 常见问题排查

### 9.1 部署失败

#### 9.1.1 构建失败

**错误信息**：
```
Build failed: Could not compile project
```

**解决方案**：
1. 检查本地编译是否成功：`mvn clean package -DskipTests`
2. 检查 GitHub 仓库代码是否最新
3. 查看构建日志具体错误
4. 确保 pom.xml 依赖正确

#### 9.1.2 找不到主类

**错误信息**：
```
Error: Unable to find or load main class com.example.qa.QaPlatformApplication
```

**解决方案**：
1. 检查 pom.xml 的 maven-compiler-plugin 配置
2. 确保主类路径正确
3. 检查 Spring Boot Maven 插件配置

### 9.2 启动失败

#### 9.2.1 端口绑定失败

**错误信息**：
```
Port 8080 is already in use
Address already in use
```

**解决方案**：
1. 使用 `${PORT:8080}` 引用环境变量
2. 确保启动命令正确
3. 检查是否有多余的进程

#### 9.2.2 数据库连接失败

**错误信息**：
```
Communications link failure
Connection refused
```

**解决方案**：
1. 检查 DB_HOST, DB_PORT 等环境变量是否正确
2. 确认 Railway MySQL 数据库是否可用
3. 检查 Railway MySQL 连接配置
4. 确认数据库用户名和密码正确

#### 9.2.3 内存不足

**错误信息**：
```
OutOfMemoryError: Java heap space
```

**解决方案**：
1. 降低 JVM 内存参数：
   ```
   java -Xmx200m -Xms64m -XX:MaxMetaspaceSize=96m -jar target/qa-platform.jar
   ```
2. 减少 HikariCP 连接池大小
3. 禁用不必要的功能

### 9.3 性能问题

#### 9.3.1 冷启动慢

**现象**：
- 首次访问需要 30-50 秒
- 免费实例休眠后需要唤醒

**解决方案**：
1. 这是 Render 免费版的正常行为
2. 考虑升级到付费实例避免休眠
3. 配置健康检查防止实例休眠

#### 9.3.2 响应慢

**可能原因**：
- 数据库查询效率低
- 网络延迟
- 实例性能不足

**解决方案**：
1. 优化数据库查询
2. 添加缓存
3. 升级实例类型

### 9.4 数据库问题

#### 9.4.1 认证失败

**错误信息**：
```
Access denied for user 'xxx'@'%' (using password: YES)
```

**解决方案**：
1. 在 Railway 重新获取数据库密码
2. 更新 DB_PASSWORD 环境变量
3. 确认用户名拼写正确

#### 9.4.2 连接数超限

**错误信息**：
```
Too many connections
```

**解决方案**：
1. 减少 HikariCP maximum-pool-size
2. 检查是否有连接泄漏
3. Railway MySQL 免费版最大 100 连接

---

## 10. 维护与监控

### 10.1 日常维护

#### 10.1.1 代码更新

```bash
# 本地修改代码后
git add .
git commit -m "Update: xxx"
git push origin main

# Render 会自动检测并重新部署
```

#### 10.1.2 数据库备份

Railway MySQL 自动提供：
- 每日自动备份
- 7 天备份历史
- 无需手动备份

### 10.2 监控

#### 10.2.1 Render 监控

在 Web Service 控制台查看：
- 请求统计
- 响应时间
- 错误率
- 实例状态

#### 10.2.2 数据库监控

在 Railway 控制台查看：
- 查询性能
- 连接数
- 存储使用
- 资源使用

### 10.3 成本控制

#### 10.3.1 免费套餐限制

**Render Free**：
- 0.5 CPU, 512MB RAM
- 每月 500 小时（休眠时不计）
- 自动休眠
- 无 SLA

**PlanetScale Develop**：
- 10GB 存储
- 1000万读/月
- 100万写/月
- 最大 100 连接

#### 10.3.2 升级建议

如果项目需要更高性能：
- Render: 升级到 Starter ($7/月)
- PlanetScale: 升级到 Scaler ($29/月起)

---

## 📋 快速检查清单

### 部署前检查
- [ ] GitHub 账户已注册，仓库已创建
- [ ] 代码已推送到 GitHub
- [ ] PlanetScale 数据库已创建
- [ ] 数据库连接凭证已记录
- [ ] application-prod.yml 配置正确
- [ ] 健康检查端点已实现

### Render 配置
- [ ] Web Service 创建完成
- [ ] GitHub 仓库已连接
- [ ] 构建命令正确
- [ ] 启动命令包含 JVM 参数
- [ ] 6 个环境变量已配置
- [ ] 健康检查路径设置

### 部署后验证
- [ ] 部署状态为 Live
- [ ] 日志显示启动成功
- [ ] /health 返回 200
- [ ] 首页可访问
- [ ] 数据库连接正常
- [ ] 核心功能测试通过

---

## ⚠️ 重要提醒

1. **数据库密码**：创建后立即保存，关闭弹窗后无法再次查看
2. **冷启动延迟**：Render 免费实例休眠后需要 30-50 秒唤醒
3. **配额限制**：PlanetScale 免费版有读写次数限制
4. **SSL 必须**：PlanetScale 要求所有连接使用 SSL
5. **端口配置**：Render 自动设置 PORT 环境变量
6. **内存限制**：JVM 内存总和必须小于 512MB

---

## 📚 参考资源

- [Render 官方文档](https://render.com/docs)
- [PlanetScale 官方文档](https://docs.planetscale.com)
- [Spring Boot 外部配置](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)
- [MySQL JDBC 参数](https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html)

---

**版本**：v2.1
**更新日期**：2026-04-03
**适用项目**：Spring Boot + MySQL 部署
**部署方案**：Render (应用) + PlanetScale (数据库)
**Happy coding!** 🚀
