# Railway 部署环境变量配置指南

## 概述

本项目已配置为使用环境变量来管理生产环境的敏感信息和数据库连接配置。

---

## 已创建的文件

### 1. application-prod.yml
位置：`src/main/resources/application-prod.yml`

功能：
- 使用环境变量替代硬编码配置
- 包含生产环境优化设置
- 配置日志输出

### 2. pom.xml 优化
- 添加了 maven-compiler-plugin 配置
- 指定了 spring-boot-maven-plugin 的 mainClass
- 设置了最终打包文件名：qa-platform.jar

---

## 必需的环境变量

### Railway 平台变量

| 变量名 | 必需 | 默认值 | 说明 |
|--------|------|--------|------|
| `PORT` | 是 | 8080 | Railway 分配的端口，必须使用此变量 |
| `RAILWAY_ENVIRONMENT` | 否 | - | Railway 环境标识 |

### 数据库连接变量

| 变量名 | 必需 | 默认值 | 说明 |
|--------|------|--------|------|
| `DB_HOST` | 是 | - | MySQL 数据库主机地址 |
| `DB_PORT` | 是 | 3306 | MySQL 数据库端口 |
| `DB_NAME` | 是 | - | 数据库名称 |
| `DB_USERNAME` | 是 | root | 数据库用户名 |
| `DB_PASSWORD` | 是 | - | 数据库密码 |

### 应用配置变量

| 变量名 | 必需 | 默认值 | 说明 |
|--------|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | 是 | prod | 激活的生产环境配置 |
| `JAVA_OPTS` | 否 | -Xmx256m | JVM 内存配置 |

---

## Railway 环境变量配置步骤

### 步骤 1：进入项目设置

1. 登录 Railway 控制台：https://railway.app/dashboard
2. 选择您的项目
3. 点击 **Settings** 标签

### 步骤 2：添加环境变量

1. 点击 **Variables** 标签
2. 逐个添加以下变量（点击 + Add Variable 按钮）：

#### 基础配置
```
PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

#### 数据库配置（根据 Railway MySQL 提供的信息填写）
```
DB_HOST=containers-us-west-xxx.railway.app
DB_PORT=xxxxx
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=xxxxxxxxxxxxxx
```

### 步骤 3：验证配置

添加完所有变量后，点击 **Deploy** 按钮重新部署。

---

## Railway MySQL 获取步骤

### 步骤 1：创建 MySQL 数据库

1. 在项目中点击 **New**
2. 选择 **Database**
3. 选择 **Add MySQL**

### 步骤 2：获取连接信息

1. 点击新创建的 MySQL 数据库
2. 点击 **Connect** 标签
3. 复制连接信息并填入环境变量

连接信息示例：
```
Host: containers-us-west-12345.railway.app
Port: 54321
Database: railway
Username: root
Password: abcdefghijklmnop
```

### 步骤 3：在 application-prod.yml 中的对应关系

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

---

## 数据库初始化

### 方法 1：使用 Railway Playground

1. 点击 MySQL 数据库
2. 点击 **Playground** 标签
3. 在编辑器中执行 `src/main/resources/qa_platform.sql` 的内容
4. 执行 `src/main/resources/message_table.sql` 的内容
5. 执行 `src/main/resources/system_config.sql` 的内容

### 方法 2：使用 MySQL Workbench

1. 下载并安装 MySQL Workbench
2. 创建新连接，填写 Railway 提供的连接信息
3. 打开 `src/main/resources/qa_platform.sql`
4. 执行 SQL 文件

### 方法 3：启用自动初始化

在 `application-prod.yml` 中启用：
```yaml
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:qa_platform.sql,classpath:message_table.sql,classpath:system_config.sql
```

> 注意：如果使用此方法，需要将 SQL 文件放在 `src/main/resources` 目录下

---

## 启动命令配置

### 在 Railway 中设置

**Build Command（构建命令）**：
```bash
mvn clean package -DskipTests
```

**Start Command（启动命令）**：
```bash
java -Dserver.port=$PORT -jar target/qa-platform.jar --spring.profiles.active=prod
```

或使用环境变量方式：
```bash
java -Dserver.port=${PORT:-8080} -jar target/qa-platform.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}
```

### 完整的环境变量列表（复制使用）

```
PORT=8080
SPRING_PROFILES_ACTIVE=prod
DB_HOST=your-mysql-host.railway.app
DB_PORT=your-mysql-port
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=your-mysql-password
```

---

## 验证部署

### 检查日志

在 Railway 控制台的 **Deployments** 标签中：
1. 选择最新的部署
2. 查看 **Build Logs**（构建日志）
3. 查看 **Runtime Logs**（运行日志）

### 常见启动日志

#### 成功示例
```
2026-03-28 10:00:00.123  INFO 12345 --- [           main] c.e.qa.QaPlatformApplication         : Starting QaPlatformApplication...
2026-03-28 10:00:05.456  INFO 12345 --- [           main] c.e.qa.QaPlatformApplication         : Started QaPlatformApplication in 5.234 seconds
2026-03-28 10:00:05.789  INFO 12345 --- [           main] o.s.b.web.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080
```

#### 失败示例

**数据库连接失败**：
```
2026-03-28 10:00:03.456  ERROR 12345 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Exception during pool initialization.
com.mysql.cj.exceptions.CJDException: Unable to connect to database
```

解决：检查 `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` 是否正确

**端口绑定失败**：
```
2026-03-28 10:00:02.345  WARN 12345 --- [           main] o.s.b.web.embedded.tomcat.TomcatWebServer  : Tomcat failed to start on port(s)...
```

解决：确保使用 `$PORT` 环境变量而非硬编码端口

---

## 故障排除

### 问题 1：构建失败 - Maven 依赖超时

**解决**：
1. 重试部署
2. 或在项目根目录添加 `settings.xml` 使用国内 Maven 镜像

### 问题 2：启动失败 - 找不到主类

**解决**：
确保 `pom.xml` 中正确配置了 `mainClass`：
```xml
<configuration>
    <mainClass>com.example.qa.QaPlatformApplication</mainClass>
</configuration>
```

### 问题 3：数据库连接被拒绝

**解决**：
1. 确认 MySQL 数据库已启动
2. 检查 `DB_HOST` 和 `DB_PORT` 是否正确
3. 确认 Railway MySQL 允许外部连接

### 问题 4：应用内存溢出

**解决**：
减少 JVM 堆内存，启动命令改为：
```bash
java -Xmx128m -Dserver.port=$PORT -jar target/qa-platform.jar
```

---

## 文档版本

- **版本**：1.0
- **创建日期**：2026-03-28
- **适用项目**：QA Platform
- **部署平台**：Railway