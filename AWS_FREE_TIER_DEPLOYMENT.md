# AWS 免费套餐部署详细指南

## 目录
1. [AWS免费套餐概述](#aws免费套餐概述)
2. [注册和账户设置](#注册和账户设置)
3. [免费套餐选择](#免费套餐选择)
4. [避免收费的关键措施](#避免收费的关键措施)
5. [费用监控和告警设置](#费用监控和告警设置)
6. [详细部署步骤](#详细部署步骤)
7. [常见收费陷阱](#常见收费陷阱)
8. [账户安全和预算控制](#账户安全和预算控制)

---

## AWS免费套餐概述

### AWS免费套餐（12个月）详情

#### 免费套餐包含
- **计算资源**：
  - 750小时/月的 t2.micro 或 t3.micro 实例
  - 相当于：1核CPU，1GB内存
  - **注意**：仅限12个月免费

- **存储资源**：
  - 5GB标准存储（EBS）
  - 20GB对象存储（S3）
  - 1GB快照存储（EBS Snapshots）

- **数据库资源**：
  - 750小时/月的 db.t2.micro 实例
  - 20GB通用型SSD存储

- **数据传输**：
  - 每月100GB出站数据传输
  - 入站数据免费

- **其他服务**：
  - 100万次Lambda请求
  - 100万次DynamoDB请求
  - 25GB DynamoDB存储

#### 收费开始时间
- **免费期**：从账户激活开始计算12个月
- **到期后**：所有资源按标准费率收费
- **重要提醒**：12个月后即使不使用也会产生基础费用

---

## 注册和账户设置

### 1. 注册AWS账户

#### 详细注册步骤
1. 访问 [AWS官网](https://aws.amazon.com/cn/)
2. 点击右上角"创建AWS账户"
3. 填写注册信息：
   - **邮箱**：使用真实邮箱（建议使用Gmail或企业邮箱）
   - **密码**：强密码（至少8位，包含大小写字母、数字、特殊字符）
   - **AWS账户名称**：个人或公司名称
   - **联系方式**：真实手机号（用于身份验证）

4. 选择账户类型：
   - **个人账户**：适合个人使用
   - **专业账户**：适合企业使用
   - **推荐**：选择个人账户以避免企业审核

5. 填写联系信息：
   - **地址**：真实地址（用于税务和合规）
   - **电话号码**：用于身份验证
   - **身份验证**：
     - 可能需要上传身份证或护照照片
     - 或通过短信验证手机号

6. 选择支付方式：
   - **信用卡**：必须提供（仅用于身份验证）
   - **借记卡**：部分情况可接受
   - **重要提醒**：
     - 信用卡不会立即扣费
     - 仅在超出免费额度时才收费
     - 建议使用额度较低的信用卡

7. 选择支持计划：
   - **基础支持**：免费
   - **开发者支持**：$29/月
   - **商业支持**：$100/月
   - **推荐**：选择基础支持（免费）

8. 同意条款：
   - 阅读AWS客户协议
   - 勾选同意条款
   - 点击"创建账户并继续"

9. 邮箱验证：
   - AWS会发送验证邮件
   - 点击邮件中的验证链接
   - 验证成功后自动登录

### 2. 账户安全设置

#### 启用MFA（多因素认证）
```bash
# 登录AWS控制台后
1. 进入"IAM" → "安全凭证"
2. 找到"已激活的MFA设备"
3. 点击"激活MFA"
4. 选择认证方式：
   - 虚拟MFA设备（推荐）
   - 硬件MFA设备
5. 扫描二维码或输入密钥
6. 保存备用码（重要！）
```

#### 创建IAM用户（最佳实践）
```bash
# 不要使用root账户，创建专用IAM用户
1. 进入"IAM" → "用户" → "添加用户"
2. 设置用户名：qa-platform-deploy
3. 选择访问类型：编程访问
4. 设置权限：
   - 直接附加策略：AdministratorAccess（开发环境）
   - 或创建自定义策略（生产环境推荐）
5. 创建访问密钥：
   - 选择".csv"格式下载
   - **重要**：只显示一次，立即保存
```

---

## 免费套餐选择

### 1. EC2实例选择（关键！）

#### 正确的免费实例类型
```bash
# 必须选择以下实例类型才能享受免费套餐
- t2.micro（推荐）
- t3.micro（推荐）

# 以下实例类型会收费，不要选择！
- t2.small（收费）
- t2.medium（收费）
- m5.large（收费）
- c5.xlarge（收费）
```

#### 实例配置步骤
1. 登录AWS控制台
2. 进入"EC2"服务
3. 点击"启动实例"
4. 配置实例：
   - **名称**：qa-platform-server
   - **AMI镜像**：
     - Ubuntu Server 22.04 LTS（HVM），SSD Volume Type
     - 或 Amazon Linux 2
   - **实例类型**：
     - 点击"实例类型"下拉菜单
     - **必须选择**：t2.micro 或 t3.micro
     - **重要**：确保看到"免费套餐适用"标记
   - **密钥对**：
     - 创建新密钥对：qa-platform-key
     - 下载.pem文件并安全保存
   - **网络设置**：
     - VPC：默认VPC
     - 子网：默认子网
     - 自动分配公网IP：启用
   - **存储卷**：
     - 卷类型：通用型SSD（gp2）
     - 大小：**必须设置为8GB或以下**（免费额度内）
     - 删除终止：启用（重要！）

5. 配置安全组：
   - 创建新安全组：qa-platform-sg
   - 添加入站规则：
     - SSH（端口22）：来源IP 0.0.0.0/0
     - HTTP（端口80）：来源IP 0.0.0.0/0
     - HTTPS（端口443）：来源IP 0.0.0.0/0
     - 自定义TCP：端口8080，来源IP 0.0.0.0/0

6. 启动实例：
   - 点击"启动"
   - 等待实例状态变为"running"
   - 记录公网IP地址

### 2. RDS数据库选择（推荐）

#### 免费数据库配置
1. 进入"RDS"服务
2. 点击"创建数据库"
3. 选择引擎：
   - MySQL
   - 版本：8.0.33（与项目匹配）
4. 配置数据库：
   - **实例类型**：db.t2.micro（免费）
   - **存储**：20GB（免费额度内）
   - **数据库标识符**：qa-platform-db
   - **主用户名**：admin
   - **主密码**：设置强密码并保存
5. 网络和安全：
   - VPC：与EC2实例相同VPC
   - 公开访问：是
   - VPC安全组：选择EC2安全组
6. 连接性：
   - 附加EC2计算资源：否（节省成本）
   - 可公开访问：是
7. 维护：
   - 自动次要版本升级：是
   - 自动备份：是（7天保留期）

**重要提醒**：RDS免费套餐也是12个月，到期后会产生费用。

---

## 避免收费的关键措施

### 1. 计算资源控制

#### 严格限制实例类型
```bash
# 只使用免费实例类型
允许的实例类型：
- t2.micro（免费）
- t3.micro（免费）

禁止的实例类型：
- t2.small、t2.medium、t2.large（收费）
- t3.small、t3.medium、t3.large（收费）
- m5、c5、r5系列（收费）
- GPU实例（收费极高）

# 创建实例时的检查清单
□ 实例类型显示"免费套餐适用"
□ 实例类型是t2.micro或t3.micro
□ 存储大小在8GB以内
□ 启用了"终止时删除"
□ 没有启用EIP（弹性IP）
```

#### 存储卷控制
```bash
# EBS存储限制
免费额度：
- 8GB通用型SSD存储（gp2）
- 8GB磁存储（standard）

# 必须启用的设置
□ 删除终止：是（终止实例时自动删除存储）
□ 存储类型：gp2（性能更好且免费）
□ 存储大小：≤8GB

# 禁止的操作
× 创建额外EBS卷（收费）
× 使用快照存储（收费）
× 使用预配置IOPS SSD（收费）
× 使用磁存储（性能差且可能收费）
```

#### 弹性IP（EIP）控制
```bash
# EIP收费规则
- 未附加到运行实例的EIP：$0.005/小时（约$3.6/月）
- 附加到运行实例的EIP：免费（仅第一个）

# 必须遵守的规则
□ 不创建额外的EIP
□ 如果需要EIP，确保始终附加到实例
□ 不再使用时立即释放EIP

# 推荐方案
使用公有IP（自动分配，免费）
而不是EIP（弹性IP，收费）
```

### 2. 数据传输控制

#### 数据传输限制
```bash
# AWS免费数据传输额度
- 每月100GB出站数据传输
- 入站数据传输：无限免费

# 监控数据传输量
□ 定期检查CloudWatch数据传输指标
□ 设置数据传输告警（见下文）
□ 避免大文件下载（如果可能）
□ 使用CDN（CloudFront）优化传输成本
```

#### 避免数据传输超限的方法
```bash
# 优化数据传输
1. 启用Gzip压缩（减少传输量）
2. 使用CDN（CloudFront）缓存静态资源
3. 限制日志文件大小
4. 避免频繁的大文件传输
5. 使用AWS免费CDN额度：
   - CloudFront：每月1TB免费
   - 可以显著减少数据传输成本
```

### 3. 其他服务控制

#### 禁用不必要的服务
```bash
# 可能产生费用的服务，需要谨慎使用
× S3存储（超出20GB收费）
× CloudFront（超出1TB收费）
× Lambda（超出100万次请求收费）
× DynamoDB（超出20GB存储和100万次请求收费）
× Elastic IP（未附加时收费）
× NAT Gateway（收费）
× VPN Gateway（收费）

# 推荐使用
√ 仅使用EC2和RDS的免费套餐
√ 使用CloudWatch基础监控（免费）
√ 使用AWS Free Tier内的所有服务
```

---

## 费用监控和告警设置

### 1. 设置预算告警

#### 创建月度预算
```bash
# AWS Billing控制台设置
1. 登录AWS控制台
2. 进入"Billing and Cost Management"
3. 点击"预算"
4. 点击"创建预算"

# 预算配置
- 预算名称：AWS Free Tier Limit
- 预算金额：$0.01（设置为接近0）
- 预算周期：月度

# 预算告警设置
- 实际费用 > $0.00：发送邮件通知
- 预测费用 > $0.00：发送邮件通知
- 通知邮箱：你的注册邮箱
- 通知频率：立即

# 高级设置
- 添加成本异常检测：
  - 阈值：$5.00
  - 通知方式：邮件 + SMS
```

#### 设置服务级别告警
```bash
# 为关键服务设置告警
1. EC2实例告警：
   - 指标：EstimatedCharges
   - 阈值：>$0.00
   - 通知：邮件 + SMS

2. RDS数据库告警：
   - 指标：EstimatedCharges
   - 阈值：>$0.00
   - 通知：邮件 + SMS

3. EBS存储告警：
   - 指标：VolumeUsage
   - 阈值：>8GB
   - 通知：邮件
```

### 2. CloudWatch监控设置

#### 创建免费套餐监控仪表板
```bash
# CloudWatch配置
1. 进入"CloudWatch"服务
2. 点击"仪表板"
3. 创建新仪表板：AWS Free Tier Monitor

# 添加关键指标
1. EC2实例运行时间：
   - 指标：CPUUtilization
   - 统计周期：1小时
   - 告警：>80%持续5分钟

2. 数据传输量：
   - 指标：NetworkOut
   - 统计周期：1天
   - 告警：>3GB/天

3. EBS存储使用：
   - 指标：VolumeReadBytes + VolumeWriteBytes
   - 统计周期：1天
   - 告警：>8GB
```

### 3. 定期费用检查

#### 每日检查清单
```bash
# 每天检查一次费用
□ 登录AWS Billing控制台
□ 查看"本月费用"（Current Month Charges）
□ 检查是否有异常费用
□ 检查免费套餐使用情况
□ 查看费用预测（Cost Explorer）
```

#### 每周检查清单
```bash
# 每周详细检查
□ 查看费用明细（Cost Explorer）
□ 分析费用趋势
□ 检查是否有未预期的服务使用
□ 检查数据传输量
□ 检查存储使用量
```

#### 每月检查清单
```bash
# 每月全面检查
□ 查看月度账单
□ 对比免费套餐使用情况
□ 检查是否超出免费额度
□ 分析费用构成
□ 调整下月预算
□ 检查12个月免费期剩余时间
```

---

## 详细部署步骤

### 1. 连接EC2实例

#### 使用SSH密钥连接
```bash
# Windows用户
1. 下载PuTTY或使用Windows Terminal
2. 转换.pem密钥为.ppk格式（使用PuTTYgen）
3. 连接到实例：
   - Host Name：你的EC2公网IP
   - Port：22
   - Connection type：SSH
   - Auth file：转换后的.ppk文件

# Mac/Linux用户
1. 确保.pem文件权限正确
chmod 400 qa-platform-key.pem

2. 连接到实例
ssh -i qa-platform-key.pem ubuntu@your_ec2_public_ip

3. 首次登录可能需要接受主机指纹
输入：yes
```

### 2. 初始服务器环境

#### 系统更新和基础工具安装
```bash
# 更新系统包
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y curl wget git vim unzip htop

# 设置时区
sudo timedatectl set-timezone Asia/Shanghai

# 配置防火墙
sudo ufw allow OpenSSH
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 8080
sudo ufw enable
```

### 3. 安装Java和Maven
```bash
# 安装OpenJDK 11
sudo apt install -y openjdk-11-jdk

# 验证Java安装
java -version

# 下载并安装Maven
cd /opt
sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
sudo tar -xzf apache-maven-3.9.5-bin.tar.gz
sudo ln -s /opt/apache-maven-3.9.5 /opt/maven

# 配置环境变量
echo 'export MAVEN_HOME=/opt/maven' >> ~/.bashrc
echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# 验证Maven安装
mvn -version
```

### 4. 配置RDS数据库连接

#### 获取RDS连接信息
```bash
# 在AWS RDS控制台获取以下信息：
1. 数据库终端点（Endpoint）
2. 端口号（通常是3306）
3. 数据库名称
4. 主用户名和密码

# 测试数据库连接
mysql -h your-rds-endpoint.rds.amazonaws.com -P 3306 -u admin -p
```

### 5. 部署应用

#### 创建生产配置文件
```bash
# 创建配置目录
mkdir -p /home/ubuntu/qa-platform/config

# 创建生产环境配置
vim /home/ubuntu/qa-platform/config/application-prod.yml
```

```yaml
spring:
  datasource:
    # 使用RDS终端点
    url: jdbc:mysql://your-rds-endpoint.rds.amazonaws.com:3306/qa_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: admin
    password: YourStrongPassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000
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
      static-locations: classpath:/static/,file:/home/ubuntu/qa-platform/uploads/
      cache:
        period: 86400
  jmx:
    enabled: false

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
      table-prefix:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
    banner: false
    enable-auto-merge-sql: true

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html

server:
  port: 8080
  address: 0.0.0.0

file:
  upload:
    path: /home/ubuntu/qa-platform/uploads/
```

#### 上传和打包项目
```bash
# 方式一：从本地上传
# 在本地打包
mvn clean package -DskipTests

# 上传到服务器
scp -i qa-platform-key.pem target/qa-platform-1.0-SNAPSHOT.jar ubuntu@your_ec2_ip:/home/ubuntu/qa-platform/

# 方式二：在服务器上构建
# 克隆项目（如果有Git仓库）
cd /home/ubuntu/qa-platform
git clone your_repository_url .

# 或上传项目文件
# 使用FileZilla上传整个项目文件夹

# 在服务器上打包
cd /home/ubuntu/qa-platform
mvn clean package -DskipTests
```

#### 创建系统服务
```bash
# 创建systemd服务文件
sudo vim /etc/systemd/system/qa-platform.service
```

```ini
[Unit]
Description=QA Platform Application
After=syslog.target network.target

[Service]
User=ubuntu
Group=ubuntu
WorkingDirectory=/home/ubuntu/qa-platform
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /home/ubuntu/qa-platform/qa-platform-1.0-SNAPSHOT.jar --spring.profiles.active=prod
ExecStop=/bin/kill -15 $MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=qa-platform

[Install]
WantedBy=multi-user.target
```

```bash
# 启动服务
sudo systemctl daemon-reload
sudo systemctl start qa-platform
sudo systemctl enable qa-platform

# 查看服务状态
sudo systemctl status qa-platform

# 查看日志
sudo journalctl -u qa-platform -f
```

---

## 常见收费陷阱

### 1. 实例类型陷阱

#### 陷阱描述
```bash
# 常见错误：选择了看似便宜但实际收费的实例类型
错误选择：
- t2.small（看起来便宜，但收费）
- t3.nano（看起来更小，但不在免费套餐内）

# 正确选择：
- t2.micro（免费套餐内）
- t3.micro（免费套餐内）

# 如何识别免费实例
在EC2控制台创建实例时：
□ 看到"免费套餐适用"标记
□ 实例类型旁边显示"免费套餐"
□ 实例估算费用显示"$0.00"
```

### 2. 存储陷阱

#### 陷阱描述
```bash
# 常见错误：创建了额外存储卷
错误操作：
- 创建额外的EBS卷（收费）
- 创建快照（收费）
- 使用预配置IOPS（收费）

# 正确操作：
- 仅使用默认的8GB存储
- 启用"终止时删除"
- 不创建快照（除非必要）
```

### 3. 数据传输陷阱

#### 陷阱描述
```bash
# 常见错误：超出免费数据传输额度
错误场景：
- 频繁下载大文件
- 提供大文件下载服务
- 数据备份到本地（大量传输）

# 正确操作：
- 使用CDN（CloudFront）
- 启用Gzip压缩
- 限制日志文件大小
- 监控数据传输量
```

### 4. 服务使用陷阱

#### 陷阱描述
```bash
# 常见错误：启用了额外AWS服务
错误操作：
- 启用了S3存储（超出20GB收费）
- 启用了Lambda函数（超出100万次请求收费）
- 创建了EIP但未附加到实例（收费）
- 启用了NAT Gateway（收费）

# 正确操作：
- 仅使用EC2和RDS的免费套餐
- 使用CloudWatch基础监控（免费）
- 不启用任何额外服务
```

---

## 账户安全和预算控制

### 1. 账户安全设置

#### MFA强制启用
```bash
# 必须启用的安全措施
□ MFA（多因素认证）：必须启用
□ 强制MFA：在IAM中设置
□ 备用码：安全保存
□ 定期更换MFA设备
```

#### IAM权限最小化
```bash
# IAM用户权限原则
- 最小权限原则：仅授予必要的权限
- 定期审查权限
- 删除不需要的用户
- 轮换访问密钥
```

### 2. 预算和费用控制

#### 设置硬性预算限制
```bash
# AWS Budget设置
1. 创建预算：$0.01（接近0）
2. 设置告警：>$0.00立即通知
3. 设置预测告警：>$0.00
4. 通知方式：邮件 + SMS

# 硬性限制（可选）
- 设置成本异常检测：$5.00
- 设置服务级别限制：每个服务$0.00
- 设置每日预算：$0.00
```

#### 定期费用审查
```bash
# 费用审查时间表
每日：检查当前费用
每周：分析费用趋势
每月：全面费用审计
每季：调整预算和策略
每年：评估AWS使用策略
```

### 3. 自动化监控脚本

#### 创建费用监控脚本
```bash
# 创建监控脚本
vim /home/ubuntu/aws-cost-monitor.sh
```

```bash
#!/bin/bash

# 配置
ALERT_EMAIL="your_email@example.com"
AWS_CLI="/usr/local/bin/aws"
THRESHOLD=0.01

# 获取当前月度费用
CURRENT_COST=$($AWS_CLI ce get-cost-and-usage --time-period Month --query 'TotalCost' --output text)

# 检查是否超过阈值
if (( $(echo "$CURRENT_COST > $THRESHOLD" | bc -l) )); then
    echo "WARNING: Current AWS cost is $CURRENT_COST, exceeding threshold of $THRESHOLD"
    # 发送邮件告警（需要配置sendmail或类似工具）
    echo "AWS Cost Alert: Current cost is $CURRENT_COST" | mail -s "AWS Cost Alert" $ALERT_EMAIL
fi

# 检查免费套餐使用情况
FREE_TIER_USAGE=$($AWS_CLI ce get-cost-and-usage --time-period Month --query 'FreeTierUsage' --output text)
echo "Free Tier Usage: $FREE_TIER_USAGE"
```

```bash
# 添加执行权限
chmod +x /home/ubuntu/aws-cost-monitor.sh

# 添加定时任务（每天检查一次）
crontab -e

# 添加以下行
0 8 * * * /home/ubuntu/aws-cost-monitor.sh >> /home/ubuntu/aws-cost-monitor.log 2>&1
```

### 4. 自动停止服务的脚本（禁止收费的关键措施）

#### 创建自动停止EC2实例脚本
```bash
# 创建自动停止脚本
vim /home/ubuntu/auto-stop-services.sh
```

```bash
#!/bin/bash

# 配置
INSTANCE_ID="i-xxxxxxxxxxxxxxxxx"  # 你的EC2实例ID
REGION="us-east-1"  # 你的AWS区域
ALERT_EMAIL="your_email@example.com"
AWS_CLI="/usr/local/bin/aws"
MAX_MONTHLY_COST=0.01

# 获取当前月度费用
CURRENT_COST=$($AWS_CLI ce get-cost-and-usage \
    --time-period Start=$(date -d "$(date +%Y-%m-01)" +%Y-%m-%d),End=$(date +%Y-%m-%d) \
    --query 'TotalCost' \
    --output text)

echo "Current cost: $CURRENT_COST"

# 检查是否超过阈值
if (( $(echo "$CURRENT_COST > $MAX_MONTHLY_COST" | bc -l) )); then
    echo "WARNING: Cost exceeded threshold! Stopping EC2 instance..."
    
    # 停止EC2实例
    $AWS_CLI ec2 stop-instances \
        --instance-ids $INSTANCE_ID \
        --region $REGION
    
    # 发送告警邮件
    echo "AWS Cost Alert: Cost exceeded $MAX_MONTHLY_COST. EC2 instance $INSTANCE_ID has been stopped." | \
        mail -s "AWS Instance Stopped - Cost Alert" $ALERT_EMAIL
    
    echo "Instance stopped successfully"
else
    echo "Cost within acceptable range"
fi
```

```bash
# 添加执行权限
chmod +x /home/ubuntu/auto-stop-services.sh

# 添加定时任务（每天检查一次，如果超支则停止服务）
crontab -e

# 添加以下行（每天凌晨2点检查）
0 2 * * * /home/ubuntu/auto-stop-services.sh >> /home/ubuntu/auto-stop-services.log 2>&1
```

#### 创建Lambda函数自动停止服务（推荐方案）

```bash
# 创建Lambda函数部署包
mkdir -p /home/ubuntu/lambda-cost-control
cd /home/ubuntu/lambda-cost-control

# 创建Lambda函数代码
vim index.py
```

```python
import boto3
import os
from datetime import datetime, timedelta

# 配置
INSTANCE_ID = os.environ.get('INSTANCE_ID', 'i-xxxxxxxxxxxxxxxxx')
REGION = os.environ.get('REGION', 'us-east-1')
MAX_COST = float(os.environ.get('MAX_COST', '0.01'))
ALERT_EMAIL = os.environ.get('ALERT_EMAIL', 'your_email@example.com')

# AWS客户端
ec2 = boto3.client('ec2', region_name=REGION)
ce = boto3.client('ce', region_name=REGION)
ses = boto3.client('ses', region_name=REGION)

def get_current_cost():
    """获取当前月度费用"""
    end_date = datetime.now().strftime('%Y-%m-%d')
    start_date = datetime.now().replace(day=1).strftime('%Y-%m-%d')
    
    response = ce.get_cost_and_usage(
        TimePeriod={
            'Start': start_date,
            'End': end_date
        },
        Granularity='MONTHLY',
        Metrics=['BlendedCost']
    )
    
    return float(response['ResultsByTime'][0]['Total']['BlendedCost']['Amount'])

def stop_instance():
    """停止EC2实例"""
    try:
        response = ec2.stop_instances(InstanceIds=[INSTANCE_ID])
        print(f"Instance {INSTANCE_ID} stopped successfully")
        return True
    except Exception as e:
        print(f"Error stopping instance: {e}")
        return False

def send_alert_email(cost):
    """发送告警邮件"""
    try:
        ses.send_email(
            Source=ALERT_EMAIL,
            Destination={'ToAddresses': [ALERT_EMAIL]},
            Message={
                'Subject': {'Data': 'AWS Cost Alert - Instance Stopped'},
                'Body': {
                    'Text': {
                        'Data': f'AWS cost exceeded ${MAX_COST}. Current cost: ${cost}. '
                               f'EC2 instance {INSTANCE_ID} has been stopped automatically.'
                    }
                }
            }
        )
        print("Alert email sent successfully")
    except Exception as e:
        print(f"Error sending email: {e}")

def lambda_handler(event, context):
    """Lambda处理函数"""
    print("Starting cost check...")
    
    # 获取当前费用
    current_cost = get_current_cost()
    print(f"Current cost: ${current_cost}")
    
    # 检查是否超过阈值
    if current_cost > MAX_COST:
        print(f"Cost exceeded threshold of ${MAX_COST}")
        
        # 停止实例
        if stop_instance():
            # 发送告警邮件
            send_alert_email(current_cost)
            
            return {
                'statusCode': 200,
                'body': f'Instance stopped due to cost exceeding ${MAX_COST}'
            }
    else:
        print(f"Cost within acceptable range: ${current_cost}")
    
    return {
        'statusCode': 200,
        'body': f'Cost check completed. Current cost: ${current_cost}'
    }
```

```bash
# 创建requirements.txt
vim requirements.txt
```

```text
boto3
```

```bash
# 打包Lambda函数
zip -r lambda-cost-control.zip index.py requirements.txt

# 使用AWS CLI部署Lambda函数
aws lambda create-function \
    --function-name cost-control-lambda \
    --runtime python3.9 \
    --role arn:aws:iam::YOUR_ACCOUNT_ID:role/LambdaExecutionRole \
    --handler index.lambda_handler \
    --zip-file fileb://lambda-cost-control.zip \
    --environment Variables={INSTANCE_ID=i-xxxxxxxxxxxxxxxxx,REGION=us-east-1,MAX_COST=0.01,ALERT_EMAIL=your_email@example.com} \
    --timeout 60 \
    --memory-size 128

# 创建定时触发器（每天凌晨2点执行）
aws events put-rule \
    --name cost-control-daily \
    --schedule-expression 'cron(0 2 * * ? *)'

# 添加Lambda函数的权限
aws lambda add-permission \
    --function-name cost-control-lambda \
    --statement-id cost-control-daily \
    --action 'lambda:InvokeFunction' \
    --principal events.amazonaws.com \
    --source-arn arn:aws:events:us-east-1:YOUR_ACCOUNT_ID:rule/cost-control-daily

# 将规则连接到Lambda函数
aws events put-targets \
    --rule cost-control-daily \
    --targets Id=1,Arn=arn:aws:lambda:us-east-1:YOUR_ACCOUNT_ID:function:cost-control-lambda
```

### 5. 设置AWS Cost Anomaly Detection（成本异常检测）

#### 启用成本异常检测
```bash
# AWS控制台设置
1. 登录AWS控制台
2. 进入"Cost Explorer"
3. 点击"Cost Anomaly Detection"
4. 点击"Enable Cost Anomaly Detection"
5. 配置检测设置：
   - 监控频率：每日
   - 异常阈值：$5.00
   - 通知方式：邮件 + SNS
   - 监控服务：EC2、RDS、EBS

# 创建异常检测订阅
1. 点击"Create Anomaly Subscription"
2. 配置订阅：
   - 订阅名称：Free Tier Anomaly Alert
   - 监控范围：所有服务
   - 阈值：$5.00
   - 通知频率：立即
   - 通知方式：邮件
```

### 6. 设置服务配额限制（Service Quotas）

#### 查看和设置服务配额
```bash
# 查看EC2服务配额
1. 进入"Service Quotas"控制台
2. 选择"AWS服务" → "Amazon EC2"
3. 查看关键配额：
   - Running On-Demand t2.micro instances（默认：20个）
   - Running On-Demand t3.micro instances（默认：20个）
   - General Purpose SSD (gp2) storage（默认：20GB）

# 设置配额警报
1. 选择配额项
2. 点击"Create alarm"
3. 配置警报：
   - 阈值：使用量 > 1
   - 通知方式：邮件
   - 通知频率：立即

# 申请降低配额（可选，防止意外创建多个实例）
1. 选择配额项
2. 点击"Request quota increase"
3. 申请降低配额到1个实例
4. 填写申请理由：Free tier usage limit
```

### 7. 使用AWS Organizations SCP限制服务使用

#### 创建服务控制策略（SCP）
```bash
# 如果使用AWS Organizations，可以创建SCP限制服务使用

# 示例SCP：禁止使用除EC2和RDS免费套餐外的所有服务
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "DenyAllExceptFreeTier",
            "Effect": "Deny",
            "NotAction": [
                "ec2:RunInstances",
                "ec2:DescribeInstances",
                "ec2:StopInstances",
                "ec2:TerminateInstances",
                "rds:CreateDBInstance",
                "rds:DescribeDBInstances",
                "rds:DeleteDBInstance",
                "cloudwatch:*",
                "iam:CreateUser",
                "iam:DeleteUser",
                "iam:Get*",
                "iam:List*"
            ],
            "Resource": "*"
        },
        {
            "Sid": "DenyNonFreeTierInstances",
            "Effect": "Deny",
            "Action": [
                "ec2:RunInstances",
                "rds:CreateDBInstance"
            ],
            "NotResource": [
                "arn:aws:ec2:*:*:instance/*",
                "arn:aws:rds:*:*:db:*"
            ],
            "Condition": {
                "StringNotLike": {
                    "ec2:InstanceType": [
                        "t2.micro",
                        "t3.micro"
                    ],
                    "rds:dbInstanceClass": [
                        "db.t2.micro"
                    ]
                }
            }
        }
    ]
}
```

### 8. 设置AWS Trusted Advisor检查

#### 启用Trusted Advisor检查
```bash
# AWS控制台设置
1. 进入"Trusted Advisor"控制台
2. 启用以下检查：
   - Service Limits（服务配额检查）
   - Underutilized EC2 Instances（未充分利用的EC2实例）
   - Unassociated Elastic IP Addresses（未关联的弹性IP）
   - Idle Load Balancers（空闲的负载均衡器）

# 设置自动刷新
1. 点击"Refresh"
2. 选择"Refresh now"
3. 设置自动刷新：每周一次

# 配置通知
1. 进入"Preferences"
2. 启用邮件通知
3. 选择通知频率：每日
4. 选择通知类型：所有检查
```

---

## 总结和最佳实践

### 避免收费的黄金法则

1. **只使用免费套餐内的资源**
   - EC2：仅使用t2.micro或t3.micro
   - RDS：仅使用db.t2.micro
   - 存储：仅使用8GB EBS

2. **严格监控费用**
   - 每日检查费用
   - 设置预算告警
   - 定期审查账单

3. **禁用不必要的服务**
   - 不创建额外EIP
   - 不使用S3、Lambda等额外服务
   - 不创建快照（除非必要）

4. **优化资源使用**
   - 启用"终止时删除"
   - 使用CDN减少数据传输
   - 定期清理不使用的资源

5. **设置自动停止机制**
   - 配置Lambda函数自动停止超支服务
   - 设置成本异常检测
   - 配置服务配额限制

### 免费期管理

- **免费期开始**：账户激活日
- **免费期结束**：12个月后
- **重要提醒**：
  - 在免费期结束前1个月开始寻找替代方案
  - 设置免费期到期提醒（提前1个月）
  - 考虑迁移到其他云服务商的免费套餐

### 紧急停止服务的操作步骤

如果发现意外收费，立即执行以下操作：

```bash
# 1. 立即停止EC2实例
aws ec2 stop-instances --instance-ids i-xxxxxxxxxxxxxxxxx

# 2. 删除EBS存储卷
aws ec2 delete-volume --volume-id vol-xxxxxxxxxxxxxxxxx

# 3. 释放弹性IP（如果有）
aws ec2 release-address --allocation-id eipalloc-xxxxxxxxxxxxxxxxx

# 4. 删除RDS实例（如果有）
aws rds delete-db-instance --db-instance-identifier qa-platform-db --skip-final-snapshot

# 5. 删除快照（如果有）
aws ec2 delete-snapshot --snapshot-id snap-xxxxxxxxxxxxxxxxx

# 6. 检查并删除其他资源
aws ec2 describe-instances --query 'Reservations[*].Instances[*].[InstanceId,State.Name,InstanceType]'
aws rds describe-db-instances --query 'DBInstances[*].[DBInstanceIdentifier,DBInstanceClass,StorageEncrypted]'
```

### 推荐的监控和告警配置

```bash
# 每日检查清单
□ 登录AWS Billing控制台
□ 查看"本月费用"（Current Month Charges）
□ 检查是否有异常费用
□ 检查免费套餐使用情况
□ 查看费用预测（Cost Explorer）

# 每周检查清单
□ 查看费用明细（Cost Explorer）
□ 分析费用趋势
□ 检查是否有未预期的服务使用
□ 检查数据传输量
□ 检查存储使用量

# 每月检查清单
□ 查看月度账单
□ 对比免费套餐使用情况
□ 检查是否超出免费额度
□ 分析费用构成
□ 调整下月预算
□ 检查12个月免费期剩余时间
```

### 联系AWS支持

如果对费用有疑问或需要帮助：

```bash
# 1. 查看账单详情
登录AWS控制台 → Billing and Cost Management → Bills

# 2. 创建支持案例
登录AWS控制台 → Support → Create case

# 3. 选择案例类型：
- Billing and Account Management
- Account and billing support

# 4. 描述问题：
- 详细说明费用问题
- 提供相关资源ID
- 说明期望的解决方案

# 5. AWS支持联系方式：
- 邮箱：aws-verification@amazon.com
- 电话：1-800-551-0878（美国）
- 在线聊天：AWS控制台 → Support → Chat
```

---

## 附录：快速参考命令

### 常用AWS CLI命令

```bash
# 查看EC2实例
aws ec2 describe-instances --query 'Reservations[*].Instances[*].[InstanceId,State.Name,InstanceType,PublicIpAddress]'

# 查看RDS实例
aws rds describe-db-instances --query 'DBInstances[*].[DBInstanceIdentifier,DBInstanceClass,AllocatedStorage]'

# 查看EBS卷
aws ec2 describe-volumes --query 'Volumes[*].[VolumeId,Size,State,Attachments[0].InstanceId]'

# 查看弹性IP
aws ec2 describe-addresses --query 'Addresses[*].[AllocationId,PublicIp,AssociationId,InstanceId]'

# 查看当前费用
aws ce get-cost-and-usage --time-period Start=$(date -d "$(date +%Y-%m-01)" +%Y-%m-%d),End=$(date +%Y-%m-%d) --query 'TotalCost' --output text

# 停止EC2实例
aws ec2 stop-instances --instance-ids i-xxxxxxxxxxxxxxxxx

# 终止EC2实例
aws ec2 terminate-instances --instance-ids i-xxxxxxxxxxxxxxxxx

# 删除RDS实例
aws rds delete-db-instance --db-instance-identifier qa-platform-db --skip-final-snapshot

# 释放弹性IP
aws ec2 release-address --allocation-id eipalloc-xxxxxxxxxxxxxxxxx

# 删除EBS卷
aws ec2 delete-volume --volume-id vol-xxxxxxxxxxxxxxxxx
```

### 常用系统管理命令

```bash
# 查看系统资源使用
htop

# 查看磁盘使用
df -h

# 查看内存使用
free -h

# 查看应用日志
sudo journalctl -u qa-platform -f

# 重启应用服务
sudo systemctl restart qa-platform

# 查看应用状态
sudo systemctl status qa-platform

# 查看防火墙状态
sudo ufw status

# 查看监听端口
sudo netstat -tlnp
```

---

## 常见问题解答

### Q1: 如何确保不会意外收费？

**A:** 采取以下措施：
1. 设置AWS Budget告警（$0.01阈值）
2. 配置Lambda函数自动停止超支服务
3. 启用Cost Anomaly Detection
4. 设置服务配额限制
5. 每日检查费用
6. 只使用免费套餐内的资源

### Q2: 免费套餐到期后会发生什么？

**A:** 免费套餐到期后：
1. 所有资源按标准费率收费
2. 即使不使用也会产生基础费用
3. 建议在到期前1个月寻找替代方案
4. 可以删除所有资源避免费用

### Q3: 如何删除所有AWS资源？

**A:** 按以下顺序删除：
1. 停止并终止EC2实例
2. 删除EBS存储卷
3. 释放弹性IP
4. 删除RDS实例
5. 删除快照
6. 删除安全组
7. 删除密钥对

### Q4: 如何查看详细的费用明细？

**A:** 使用以下方法：
1. AWS Cost Explorer：详细费用分析
2. AWS Billing控制台：查看账单
3. AWS CLI：查询费用数据
4. 设置定期费用报告

### Q5: 如果发现意外收费怎么办？

**A:** 立即执行：
1. 停止所有EC2实例
2. 删除所有EBS卷
3. 释放所有弹性IP
4. 删除RDS实例
5. 查看费用明细
6. 联系AWS支持

---

## 联系和支持

### AWS支持资源

- **AWS官方文档**：https://docs.aws.amazon.com/
- **AWS免费套餐页面**：https://aws.amazon.com/free/
- **AWS Billing支持**：https://aws.amazon.com/support/
- **AWS论坛**：https://forums.aws.amazon.com/

### 本地支持

- **项目文档**：E:\Test\XiangMu\LunTan_Demo\qa-platform\AWS_FREE_TIER_DEPLOYMENT.md
- **部署日志**：/home/ubuntu/qa-platform/logs/
- **监控日志**：/home/ubuntu/aws-cost-monitor.log

---

**文档版本**：1.0
**最后更新**：2026-03-21
**维护者**：QA Platform Team
  - 或准备迁移到其他免费平台
  - 或评估是否愿意支付费用继续使用

### 紧急停止措施

如果发现意外费用：

1. **立即停止所有EC2实例**
   ```bash
   aws ec2 stop-instances --instance-ids i-xxxxxxxxxxxxx
   ```

2. **删除所有EBS卷**
   ```bash
   aws ec2 delete-volume --volume-id vol-xxxxxxxxxxxxx
   ```

3. **释放所有EIP**
   ```bash
   aws ec2 release-address --allocation-id eipalloc-xxxxxxxxxxxxx
   ```

4. **删除所有RDS实例**
   ```bash
   aws rds delete-db-instance --db-instance-identifier qa-platform-db --skip-final-snapshot
   ```

5. **联系AWS支持**
   - 解释情况
   - 申请费用退款（如果合理）
   - 询问是否有误收费

---

## 费用参考

### AWS免费套餐费用结构

#### 免费期（12个月）
- EC2实例：$0.00（t2.micro或t3.micro）
- RDS实例：$0.00（db.t2.micro）
- EBS存储：$0.00（8GB）
- 数据传输：$0.00（100GB内）
- CloudWatch：$0.00（基础监控）

#### 免费期后（标准费率）
- EC2实例：
  - t2.micro：约$8.47/月
  - t2.small：约$16.98/月
  - t2.medium：约$33.95/月

- EBS存储：
  - gp2 SSD：约$0.08/GB/月
  - 8GB：约$0.64/月

- 数据传输：
  - 前100GB：免费
  - 超出部分：约$0.09/GB

### 免费替代方案

如果AWS免费期结束，考虑以下免费替代方案：

1. **Oracle Cloud**：永久免费
2. **Google Cloud**：$300免费额度
3. **Azure**：12个月免费
4. **Heroku**：免费套餐（有限制）
5. **Railway**：$5免费额度
6. **Render**：免费套餐（有限制）

---

## 联系和支持

### AWS支持
- 免费支持：通过AWS控制台提交案例
- 付费支持：$29/月起
- 文档：https://docs.aws.amazon.com/

### 常见问题
- 如何查看免费套餐使用情况？
  答：AWS Billing控制台 → Cost Explorer → Free Tier
- 如何设置费用告警？
  答：AWS Billing控制台 → Budgets → Create Budget
- 免费期什么时候结束？
  答：账户激活后12个月
- 如何避免意外收费？
  答：严格监控费用，设置预算告警，仅使用免费套餐内的资源

按照本文档的详细步骤和注意事项，您可以安全地使用AWS免费套餐部署QA Platform，并有效避免后续收费。
