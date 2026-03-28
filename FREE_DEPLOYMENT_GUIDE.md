# 免费部署项目到自定义域名 - 超详细图文教程

> 更新时间：2026-03-28
> 适用对象：个人开发者、小型项目、预算有限的用户

---

## 目录

1. [方案对比概览](#方案对比概览)
2. [Vercel 详细教程](#方案一-vercel最推荐)
3. [Netlify 详细教程](#方案二-netlify)
4. [Cloudflare Pages 详细教程](#方案三-cloudflare-pages最安全)
5. [GitHub Pages 详细教程](#方案四-github-pages最简单)
6. [通用防扣费策略](#通用防扣费策略)
7. [解除删除账号教程](#解除删除账号教程)

---

# 方案对比概览

| 平台 | 免费额度 | 自定义域名 | 自动HTTPS | 恶意扣费风险 | 推荐指数 |
|------|----------|------------|-----------|--------------|----------|
| **Vercel** | 100GB带宽/月 | 免费 | 自动 | 低（需注意） | ⭐⭐⭐⭐⭐ |
| **Netlify** | 100GB带宽/月 | 免费 | 自动 | 低（需注意） | ⭐⭐⭐⭐ |
| **Cloudflare Pages** | 无限 | 免费 | 自动 | 极低（最安全） | ⭐⭐⭐⭐⭐ |
| **GitHub Pages** | 无限 | 免费 | 自动 | 极低（无付费功能） | ⭐⭐⭐⭐ |

---

# 方案一：Vercel（最推荐）

## 为什么选它

- 部署简单，与GitHub深度集成
- 预览部署功能强大
- 中国大陆访问速度较快
- 自动检测主流框架（Next.js/Vue/React等）

---

## 1.1 注册账号

### 步骤 1.1.1 访问官网

```
在浏览器中访问：https://vercel.com
```

### 步骤 1.1.2 点击注册

```
点击页面右上角的 "Sign Up" 按钮
```

### 步骤 1.1.3 选择注册方式

**方式 A: GitHub 授权（最简单）**

```
1. 点击 "Continue with GitHub"
2. 在GitHub授权页面点击 "Authorize vercel"
3. 完成！
```

**方式 B: 邮箱注册**

```
1. 点击 "Continue with Email"
2. 输入邮箱地址（如 example@gmail.com）
3. 点击 "Continue"
4. 去邮箱查收验证邮件
5. 点击邮件中的验证链接
```

### 步骤 1.1.4 完成个人资料

```
注册完成后，会引导你设置：
- Full Name（姓名）- 输入你的名字
- Country（国家）- 选择 China
- 点击 "Continue"
```

### ⚠️ 关键警示

```
【绝对不要做】
❌ 绑定信用卡
❌ 添加支付方式
❌ 填写账单信息

【正确做法】
✅ 只使用GitHub授权登录
✅ 只绑定邮箱
```

---

## 1.2 部署项目

### 方式一：通过 GitHub 部署（推荐）

#### 步骤 1.2.1 创建新项目

```
1. 登录后点击右上角 "+" 或 "Add New..."
2. 选择 "Project"（不是 "Deployment"）
```

#### 步骤 1.2.2 连接 GitHub 仓库

```
1. 在 "Import Git Repository" 页面
2. 找到你的仓库（如果没看到，点击 "Install GitHub App"）
3. 点击 "Install" 安装Vercel的GitHub应用
4. 选择授权范围：
   - 选择 "All repositories"（方便管理）
   - 或选择特定仓库
5. 点击 "Install"
```

#### 步骤 1.2.3 导入项目

```
1. 返回 Vercel 页面
2. 找到你的仓库，点击 "Import"
```

#### 步骤 1.2.4 配置项目

```
在配置页面设置：

Project Name（项目名称）
├── 输入：my-project
└── 这会成为你的子域名：my-project.vercel.app

Framework Preset（框架预设）
├── Vercel会自动检测
└── 如需手动选择：
    - Next.js
    - Create React App
    - Vue / Nuxt
    - Vite
    - Static (HTML/CSS/JS)

Root Directory（根目录）
├── 保持默认 "." 
└── 或指定子目录

Build and Output Settings
├── Build Command（构建命令）
│   ├── Next.js: npm run build
│   ├── Vite: npm run build
│   ├── CRA: npm run build
│   └── 静态: 无需设置
│
└── Output Directory（输出目录）
    ├── Next.js: .next
    ├── Vite: dist
    ├── CRA: build
    └── 静态: . (表示根目录)

Environment Variables（环境变量）
└── 如果项目需要，点击 "Add" 添加
```

#### 步骤 1.2.5 点击部署

```
配置完成后，点击右下角的 "Deploy"

等待构建过程（约1-5分钟），你会看到：
├── 第一步：Cloning（克隆代码）
├── 第二步：Building（构建项目）
├── 第三步：Deploying（部署上线）
└── 完成！🎉
```

#### 步骤 1.2.6 访问你的网站

```
部署成功后，你会看到：
├── Production URL: my-project.vercel.app
├── Preview URL: (每次PR都会生成预览链接)
└── 点击 "Visit" 打开网站
```

---

### 方式二：直接上传部署（无需GitHub）

#### 步骤 1.2.7 创建ZIP包

```
1. 在本地项目中执行构建命令
   npm run build
   
2. 找到构建输出目录（如 dist/ 或 build/）

3. 将整个目录压缩为ZIP文件
   - 右键点击文件夹
   - 选择 "发送到" > "压缩(zipped)文件夹"
```

#### 步骤 1.2.8 上传部署

```
1. Vercel首页点击 "Add New..."
2. 选择 "Static Site"
3. 点击 "Upload" 上传ZIP文件
4. 等待处理完成
5. 点击 "Visit" 访问
```

---

## 1.3 配置自定义域名

### 步骤 1.3.1 进入域名设置

```
1. 进入你的项目页面
2. 点击 "Settings"（设置）
3. 左侧菜单选择 "Domains"
```

### 步骤 1.3.2 添加域名

```
1. 在输入框中输入你的域名
   例如：example.com 或 www.example.com
   
2. 点击 "Add"

3. 选择验证方式：
   - "Add to existing domain as CNAME record"（推荐）
   - 或 "Add and configure NAMESERVER records"
```

### 步骤 1.3.3 配置DNS（域名服务商）

#### 如果你使用阿里云/万网

```
1. 登录阿里云控制台 https://dns.console.aliyun.com
2. 进入"域名解析"
3. 找到你的域名，点击"解析设置"
4. 点击"添加记录"，填写：

记录类型：CNAME
主机记录：
   - 如果添加 www.example.com：填写 www
   - 如果添加 example.com：填写 @
记录值：cname.vercel-dns.com
TTL：10分钟（默认）

5. 点击"确认"
```

#### 如果你使用腾讯云/dnspod

```
1. 登录DNSPod https://www.dnspod.cn
2. 进入"我的域名"
3. 找到你的域名，点击"解析"
4. 点击"添加记录"，填写：

记录类型：CNAME
主机记录：
   - www（添加www子域名）
   - @（添加主域名）
记录值：cname.vercel-dns.com
TTL：600秒
```

#### 如果你使用Namecheap

```
1. 登录 Namecheap
2. 进入 Dashboard > Domain List
3. 点击域名后的 "Manage"
4. 选择 "Advanced DNS"
5. 点击 "Add New Record"，选择 CNAME：

Host: www (或 @)
Value: cname.vercel-dns.com
TTL: Automatic
```

#### 如果你使用GoDaddy

```
1. 登录 GoDaddy
2. 进入 My Products > DNS
3. 点击 "Add" > 选择 "CNAME"
4. 填写：

Type: CNAME
Name: www
Value: cname.vercel-dns.com
TTL: 30 minutes
```

### 步骤 1.3.4 验证域名

```
1. 返回 Vercel Domains 页面
2. 点击域名后的 "Check Verification" 按钮
3. 等待验证通过（通常1分钟-24小时）

验证状态说明：
├── ✅ Verified - 验证成功！
├── ⏳ Pending - 等待DNS生效，稍后再试
└── ❌ Error - 检查DNS配置是否正确
```

### 步骤 1.3.5 强制HTTPS（可选但推荐）

```
1. 验证通过后
2. 点击域名右侧的 "Edit"
3. 开启 "Force HTTPS" 开关

这样所有HTTP请求都会自动跳转到HTTPS
```

### 步骤 1.3.6 测试域名

```
DNS生效后（通常10分钟-24小时），访问：
https://yourdomain.com

你应该能看到你的网站了！🎉
```

---

## 1.4 自动部署配置

### 步骤 1.4.1 配置Git钩子

```
当GitHub有新的提交时，Vercel会自动部署：

1. 项目 Settings > Git
2. 确保 "GitHub Integration" 已启用
3. 设置 "Ignored Build Step"（可选）
   用于跳过某些不需要部署的提交

示例：当package.json有变更时才部署
   git diff --staged --quiet || git diff --quiet HEAD~1 package.json
```

### 步骤 1.4.2 分支部署设置

```
在 Settings > Git 中配置：

Production Branch（生产分支）
├── 设置哪个分支的更新会发布到正式环境
└── 默认：main 或 master

Preview Branches（预览分支）
├── 设置哪些分支会生成预览链接
└── 默认：所有分支

Ignored Branches（忽略分支）
└── 设置哪些分支不会触发部署
```

---

## 1.5 解除/删除部署

### 步骤 1.5.1 删除单个部署

```
1. 进入项目页面
2. 点击 "Deployments" 标签
3. 找到要删除的部署
4. 点击右侧 "..."
5. 选择 "Delete"
6. 确认删除
```

### 步骤 1.5.2 删除整个项目

```
1. 进入项目 Settings
2. 滚到页面最底部 "Danger Zone"
3. 点击 "Delete Project"
4. 输入项目名称确认
5. 点击 "Delete"
```

### 步骤 1.5.3 注销Vercel账号（彻底解除）

```
⚠️ 这是不可逆操作！

1. 访问 https://vercel.com/account
2. 滚到最底部 "Account Deletion"
3. 点击 "Delete Account"
4. 输入密码
5. 选择删除原因（可选）
6. 点击 "Continue"
7. 去邮箱查收确认邮件
8. 点击邮件中的确认链接
9. 账号将在24小时内被永久删除
```

---

## 1.6 防扣费详细措施

### 1.6.1 设置零预算限制

```
1. 进入 https://vercel.com/account/billing
2. 点击 "Spending Limit"
3. 设置 Limit 为 $0.00
4. 点击 "Save Changes"

这样即使误操作也不会产生费用！
```

### 1.6.2 检查订阅状态

```
定期检查（建议每月一次）：

1. 进入 https://vercel.com/account/billing
2. 查看 "Subscription" 部分
3. 确认没有任何 active 订阅
4. 查看 "Usage" 部分
5. 确认在免费额度内
```

### 1.6.3 移除所有支付方式

```
1. 进入 https://vercel.com/account/billing > Payment Methods
2. 确保没有任何绑定的信用卡
3. 如果有，点击删除图标移除
```

### 1.6.4 关闭不需要的功能

```
在项目 Settings 中检查：

Functions（Serverless函数）
├── 免费额度：100小时/月
└── ⚠️ 如果超出会自动停止，不需要担心扣费

Bandwidth（带宽）
├── 免费额度：100GB/月
└── ⚠️ 超限后网站仍可访问，只是速度受限

Builds（构建次数）
├── 免费额度：3000次/月
└── ⚠️ 超限后需要等到下月重置
```

---

# 方案二：Netlify

## 为什么选它

- 拖拽即可部署
- 自动配置HTTPS
- 分支预览功能强大

---

## 2.1 注册账号

### 步骤 2.1.1 访问官网

```
浏览器访问：https://www.netlify.com
```

### 步骤 2.1.2 开始注册

```
点击右上角 "Sign up"
```

### 步骤 2.1.3 选择注册方式

**方式 A: GitHub 授权（推荐）**

```
1. 点击 "GitHub"
2. 授权页面点击 "Authorize Netlify"
3. 完成！
```

**方式 B: 邮箱注册**

```
1. 点击 "Email"
2. 输入姓名、邮箱、密码
3. 点击 "Create account"
```

### ⚠️ 关键警示

```
【绝对不要做】
❌ 绑定信用卡
❌ 升级到 Pro/Essential 等付费计划
❌ 购买任何 add-on

【正确做法】
✅ 免费账号完全足够个人项目使用
✅ 不要点击任何升级按钮
```

---

## 2.2 部署项目

### 方式一：拖拽部署（最简单）

#### 步骤 2.2.1 构建项目

```
在本地执行：
npm run build  (或其他构建命令)

找到输出目录：
├── dist/
├── build/
└── 或项目根目录（静态网站）
```

#### 步骤 2.2.2 拖拽上传

```
1. Netlify首页点击 "Add new site"
2. 选择 "Deploy manually"
3. 将构建输出文件夹拖入上传区域

等待上传和部署完成...
```

#### 步骤 2.2.3 自定义子域名

```
部署成功后：
1. 显示临时URL如：random-name-123.netlify.app
2. 点击 "Site settings"
3. 点击 "Change site name"
4. 输入你想要的名称
5. 点击 "Save"

现在你的网站是：your-name.netlify.app
```

---

### 方式二：通过Git部署

#### 步骤 2.2.4 连接仓库

```
1. 点击 "Add new site"
2. 选择 "Import an existing project"
3. 选择你的Git服务商：
   - GitHub
   - GitLab
   - Bitbucket
4. 授权访问
5. 选择要部署的仓库
```

#### 步骤 2.2.5 配置构建

```
Basic build settings：

Build command（构建命令）
├── npm run build
├── yarn build
├── hugo
└── 或其他框架命令

Publish directory（发布目录）
├── dist
├── build
├── public
└── . (根目录)
```

#### 步骤 2.2.6 部署

```
点击 "Deploy site"

Netlify会自动：
1. 克隆代码
2. 安装依赖
3. 执行构建
4. 部署上线

完成后显示部署URL
```

---

## 2.3 配置自定义域名

### 步骤 2.3.1 进入域名设置

```
1. 点击 "Domain settings"
2. 或进入 Site Settings > Domain management
```

### 步骤 2.3.2 添加域名

```
1. 点击 "Add custom domain"
2. 输入你的域名（如 example.com）
3. 点击 "Verify"

如果添加 www 子域名，Netlify会提示：
"Should we also set up www.example.com?"
选择 "Yes" 自动配置
```

### 步骤 2.3.3 配置DNS

#### 阿里云/万网

```
添加两条记录：

记录1 - 主域名：
  类型：A
  主机记录：@
  记录值：75.2.60.5
  （Netlify的IP地址）

记录2 - www子域名：
  类型：CNAME
  主机记录：www
  记录值：your-site-name.netlify.app
```

#### 腾讯云/dnspod

```
添加记录：

@ 记录：
  类型：A
  主机记录：@
  记录值：75.2.60.5

www 记录：
  类型：CNAME
  主机记录：www
  记录值：your-site-name.netlify.app
```

### 步骤 2.3.4 添加SSL证书

```
DNS配置完成后：
1. 在 Netlify 域名设置页面
2. 点击 "Verify DNS configuration"
3. 等待验证通过
4. 点击 "Provision certificate"

Netlify会自动申请 Let's Encrypt 证书
通常5分钟内完成
```

### 步骤 2.3.5 强制HTTPS重定向

```
1. 在 Domain management 页面
2. 找到你的域名
3. 点击右侧 "HTTPS" 选项
4. 开启 "Enforce HTTPS"
5. 开启 "Redirect default subdomain to primary domain"
   （防止 yoursite.netlify.app 仍可访问）
```

---

## 2.4 自动部署配置

### 步骤 2.4.1 配置构建钩子

```
1. Site Settings > Build & deploy
2. 点击 "Build hooks"
3. 点击 "Add build hook"
4. 输入名称（如 "GitHub Push"）
5. 选择要触发的分支（通常是 main）
6. 点击 "Save"
7. 复制生成的 webhook URL

在 GitHub：
Settings > Webhooks > Add webhook
Payload URL: 粘贴刚才的URL
Content type: application/json
Events: Just the push event
```

### 步骤 2.4.2 分支部署

```
Site Settings > Build & deploy > Deploy contexts

Production branch:
  设置哪个分支部署到正式环境
  默认：main

Branch subdomains:
  设置分支预览URL格式
  如：branch-name.yoursite.netlify.app
```

---

## 2.5 解除/删除部署

### 步骤 2.5.1 删除站点

```
1. Site Settings > General
2. 滚到 "Danger Zone"
3. 点击 "Delete site"
4. 输入站点名称确认
5. 点击 "Delete"
```

### 步骤 2.5.2 注销Netlify账号

```
⚠️ 不可逆操作！

1. 进入 Account Settings
2. 点击 "General"
3. 滚到最底部 "Account"
4. 点击 "Delete Netlify account"
5. 选择删除原因（可选）
6. 点击 "Delete my account"
7. 确认删除
```

---

## 2.6 防扣费详细措施

### 2.6.1 确认免费账号状态

```
1. 登录 https://app.netlify.com
2. 点击右上角头像 > User settings
3. 进入 "Billing"
4. 确认 "Current plan" 显示为 "Starter" 或 "Free"
5. 确认没有绑定的信用卡
```

### 2.6.2 检查使用量

```
在 Billing 页面查看：

Bandwidth（带宽）
└── 免费：100GB/月

Build minutes（构建时间）
└── 免费：300分钟/月

Collaborators（协作者）
└── 免费：1人

Sites（站点数）
└── 免费：无限（静态）
```

### 2.6.3 设置使用警报

```
在 Billing > Alerts 设置：
├── Email when usage reaches: 80%
└── Email when approaching build minutes limit
```

---

# 方案三：Cloudflare Pages（最安全）

## 为什么选它

- 纯免费，无任何付费功能诱导
- Cloudflare CDN全球加速
- 不存在信用卡绑定入口
- 没有付费升级选项

---

## 3.1 注册账号

### 步骤 3.1.1 访问官网

```
浏览器访问：https://pages.cloudflare.com
```

### 步骤 3.1.2 开始注册

```
点击 "Sign up"
```

### 步骤 3.1.3 填写注册信息

```
输入：
├── Email: 你的邮箱地址
├── Password: 设置密码（至少8位）
└── 点击 "Create Account"
```

### 步骤 3.1.4 验证邮箱

```
1. 去邮箱查收验证邮件
2. 点击邮件中的验证链接
3. 完成验证
```

### ✅ Cloudflare优势

```
✅ 完全不需要绑定信用卡
✅ 没有付费功能入口
✅ Pages服务完全免费
✅ 自动包含CDN加速
✅ 自动包含SSL证书
```

---

## 3.2 部署项目

### 方式一：通过GitHub部署（推荐）

#### 步骤 3.2.1 连接GitHub

```
1. 登录 Cloudflare Dashboard
2. 进入 "Workers & Pages"
3. 点击 "Create application"
4. 选择 "Pages"
5. 点击 "Connect to Git"
```

#### 步骤 3.2.2 授权GitHub

```
1. 选择 "GitHub"
2. 点击 "Authorize cloudflare-docs"
3. 选择授权范围：
   - "All repositories"（推荐）
   - 或指定特定仓库
4. 点击 "Install & Authorize"
```

#### 步骤 3.2.3 选择仓库

```
1. 在 Cloudflare 页面
2. 选择你的 GitHub 账号
3. 从列表中选择要部署的仓库
```

#### 步骤 3.2.4 配置构建

```
Pages configuration：

Project name（项目名称）
└── 输入：my-project

Production branch（生产分支）
└── 设置：main 或 master

Build settings（构建设置）
├── 选择框架或自定义
└── Framework presets（自动检测）：
    - Next.js
    - Astro
    - Vue / Nuxt
    - React / Gatsby
    - Hugo / Jekyll
    - 自定义构建

Build command（构建命令）
└── 如：npm run build

Build output directory
└── 如：dist / build / out / .

Environment variables（可选）
└── 如需要，点击 "Add variable"
```

#### 步骤 3.2.5 部署

```
点击 "Save and Deploy"

Cloudflare会自动：
1. 克隆GitHub仓库
2. 安装依赖
3. 执行构建
4. 部署到全球CDN

完成后显示：
├── .cloudflare.pages.dev 子域名
└── 自定义域名设置选项
```

---

### 方式二：直接上传

#### 步骤 3.2.6 手动上传（使用Wrangler CLI）

```
1. 安装 Wrangler CLI
   npm install -g wrangler

2. 登录 Cloudflare
   wrangler login

3. 部署文件夹
   wrangler pages deploy ./dist

4. 获取部署URL
```

---

## 3.3 配置自定义域名

### 步骤 3.3.1 添加自定义域名

```
1. 进入你的 Pages 项目
2. 点击 "Custom domains"
3. 点击 "Set up a custom domain"
4. 输入你的域名（如 example.com）
5. 点击 "Check"
```

### 步骤 3.3.2 DNS自动配置

```
Cloudflare Pages 的优势：

✅ 完全自动DNS配置！
✅ 自动申请SSL证书！
✅ 自动启用CDN！

你只需要做一件事：
在域名服务商处添加一条CNAME记录

Cloudflare会自动处理其他一切！
```

### 步骤 3.3.3 配置DNS（简化版）

#### 阿里云/万网

```
添加一条记录：

类型：CNAME
主机记录：www
记录值：你的项目.cloudflare.pages.dev
TTL：10分钟
```

#### 腾讯云/dnspod

```
添加记录：

类型：CNAME
主机记录：www
记录值：你的项目.cloudflare.pages.dev
TTL：600秒
```

#### GoDaddy

```
添加记录：

类型：CNAME
主机记录：www
指向：你的项目.cloudflare.pages.dev
TTL：30 minutes
```

### 步骤 3.3.4 等待验证

```
1. DNS配置完成后
2. Cloudflare会自动检测
3. 显示验证状态：
   - "Active ✓" 表示成功
   - 等待中，稍后再检查
```

### 步骤 3.3.5 HTTPS自动启用

```
Cloudflare Pages 自动提供：

✅ 免费SSL证书
✅ 自动HTTPS重定向
✅ HTTP/2 和 HTTP/3 支持

无需任何额外配置！
```

---

## 3.4 自动部署配置

### 步骤 3.4.1 配置构建触发

```
Cloudflare Pages 默认：
✅ GitHub推送自动触发构建
✅ 每个PR自动生成预览部署
✅ 合并到main自动部署到生产环境

无需额外配置！
```

### 步骤 3.4.2 管理构建分支

```
项目 Settings > Builds and deployments：

Production branch（生产分支）
└── 设置哪个分支部署到正式环境

Preview branchs（预览分支）
└── 设置哪些分支生成预览链接

Protection（保护设置）
└── ⚠️ 这是免费功能！
    ✓ 包含构建超时设置
```

---

## 3.5 解除/删除部署

### 步骤 3.5.1 删除Pages项目

```
1. 进入 Cloudflare Dashboard
2. 进入 Workers & Pages
3. 选择你的项目
4. 点击 "Overview"
5. 滚到最底部 "Danger Zone"
6. 点击 "Delete project"
7. 输入项目名称确认
8. 点击 "Delete"
```

### 步骤 3.5.2 注销Cloudflare账号

```
⚠️ 会影响所有Cloudflare服务（包括其他域名）

1. 登录 https://dash.cloudflare.com
2. 点击右上角头像 > Profile
3. 选择 "Authentication"
4. 滚到 "Make Deletion Request"
5. 阅读注意事项
6. 点击 "Delete account"
7. 选择删除原因
8. 点击 "Delete Account"
9. 确认删除
```

---

## 3.6 防扣费详细措施

### ✅ Cloudflare Pages 是最安全的选择

```
为什么最安全：

✅ 没有信用卡绑定入口
✅ 没有付费升级选项
✅ 没有订阅功能
✅ Pages服务永久免费
✅ 包含无限带宽
✅ 包含全球CDN
✅ 包含SSL证书

即使你想付费也无法为Pages付费！
```

### 3.6.1 安全检查清单

```
每月检查一次：

□ 登录 Cloudflare Dashboard
□ 确认只有 Pages 相关的服务
□ 确认没有购买其他 Cloudflare 产品
□ 检查账户设置没有变更
```

---

# 方案四：GitHub Pages（最简单）

## 为什么选它

- 完全免费
- 无需信用卡
- 与GitHub深度集成
- 无限流量

---

## 4.1 注册账号（如果还没有GitHub）

### 步骤 4.1.1 访问GitHub

```
浏览器访问：https://github.com
```

### 步骤 4.1.2 注册

```
1. 点击 "Sign up"
2. 输入邮箱地址
3. 点击 "Continue"
4. 设置密码（至少8位，含数字和字母）
5. 设置用户名（会显示在yourname.github.io中）
6. 选择是否接收邮件（可选）
7. 点击 "Create account"
```

### 步骤 4.1.3 验证邮箱

```
1. 去邮箱查收验证邮件
2. 点击邮件中的验证链接
3. 完成人机验证
```

---

## 4.2 准备项目

### 步骤 4.2.1 创建仓库

```
1. 登录 GitHub
2. 点击右上角 "+" > "New repository"
```

### 步骤 4.2.2 配置仓库

```
Create a new repository：

Repository name
└── 输入：my-website

Description（可选）
└── 输入：我的个人网站

Public / Private
└── ⚠️ Public 才能使用 GitHub Pages！
└── 选择：Public

☑️ Add a README file
└── （建议勾选，初始化仓库）

.gitignore（可选）
└── 选择：None 或 对应框架

License（可选）
└── 选择：MIT 或其他

点击 "Create repository"
```

### 步骤 4.2.3 上传代码

**方式 A：网页上传（适合小项目）**

```
1. 在仓库页面点击 "uploading an existing file"
2. 拖拽你的文件到上传区域
3. 点击 "Commit changes"
```

**方式 B：Git命令（适合大项目）**

```
1. 在本地项目目录打开终端
2. 执行：
   git init
   git remote add origin https://github.com/用户名/仓库名.git
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git push -u origin main
```

### 步骤 4.2.4 确保有入口文件

```
GitHub Pages 要求项目根目录有：

静态网站（HTML）：
  必须有 index.html

Jekyll：
  可以有 _config.yml
  自动识别 Jekyll 项目

如果上传的是构建产物：
  确保 dist/ 或 build/ 目录中有 index.html
  而不是源代码目录
```

---

## 4.3 启用GitHub Pages

### 步骤 4.3.1 进入仓库设置

```
1. 进入你的仓库页面
2. 点击 "Settings"（不是个人设置）
```

### 步骤 4.3.2 找到Pages设置

```
1. 在左侧菜单找到 "Pages"
2. 点击进入
```

### 步骤 4.3.3 配置Pages

```
GitHub Pages：

Source（来源）
└── 点击下拉菜单，选择：

└── ✓ Deploy from a branch

Branch（分支）
├── Branch: main 或 master
└── / (root) 或 /docs

Folder（文件夹）
├── / (root) - 仓库根目录
├── /docs - docs 文件夹
└── /dist - 构建输出目录

选择后点击 "Save"
```

### 步骤 4.3.4 等待部署

```
1. 保存后页面会刷新
2. 显示：
   "Your site is published at https://username.github.io/repo-name/"
   
3. 首次部署需要 2-10 分钟

4. 刷新页面直到看到绿色的 ✓
   "Your site is live at..."
```

---

## 4.4 配置自定义域名

### 步骤 4.4.1 进入域名设置

```
1. Settings > Pages
2. 找到 "Custom domain"
3. 输入你的域名（如 example.com）
4. 点击 "Save"
```

### 步骤 4.4.2 选择域名类型

**添加主域名（example.com）**

```
在 Custom domain 输入框输入：example.com
点击 "Save"

然后需要添加 DNS 记录
```

**添加www域名（www.example.com）**

```
在 Custom domain 输入框输入：www.example.com
点击 "Save"
```

### 步骤 4.4.3 配置DNS

#### 方式一：使用A记录（推荐主域名）

##### 阿里云/万网

```
添加4条A记录：

记录1：
  类型：A
  主机记录：@
  记录值：185.199.108.153
  TTL：10分钟

记录2：
  类型：A
  主机记录：@
  记录值：185.199.109.153
  TTL：10分钟

记录3：
  类型：A
  主机记录：@
  记录值：185.199.110.153
  TTL：10分钟

记录4：
  类型：A
  主机记录：@
  记录值：185.199.111.153
  TTL：10分钟
```

##### 腾讯云/dnspod

```
添加4条A记录：

@ - A - 185.199.108.153
@ - A - 185.199.109.153
@ - A - 185.199.110.153
@ - A - 185.199.111.153
TTL：600秒
```

#### 方式二：使用CNAME（推荐www）

##### 阿里云/万网

```
添加CNAME记录：

类型：CNAME
主机记录：www
记录值：用户名.github.io
TTL：10分钟
```

##### 腾讯云/dnspod

```
添加CNAME记录：

www - CNAME - 用户名.github.io
TTL：600秒
```

### 步骤 4.4.4 启用HTTPS

```
DNS配置完成后：

1. 在 Pages 设置页面
2. 勾选 "Enforce HTTPS"
3. GitHub 会自动申请 Let's Encrypt 证书
4. 等待证书生效（通常15分钟内）
```

### 步骤 4.4.5 验证域名

```
1. 返回 Settings > Pages
2. 等待域名状态显示：
   ✓ DNS配置成功
   
3. 自定义域名处应显示：
   ✓ example.com - Ready - HTTPS
```

---

## 4.5 自动部署配置

### 步骤 4.5.1 GitHub Actions自动部署

```
如果使用自定义构建流程（如npm run build）：

1. 在仓库中创建 .github/workflows/deploy.yml

2. 写入以下内容：

name: GitHub Pages Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Install dependencies
        run: npm ci
        
      - name: Build
        run: npm run build
        
      - name: Deploy to GitHub Pages
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./dist
```

### 步骤 4.5.2 Jekyll自动构建

```
如果使用Jekyll：

1. 确认仓库根目录有 _config.yml
2. GitHub Pages会自动：
   - 安装依赖（Gemfile）
   - 构建 Jekyll 站点
   - 部署 _site 目录

无需额外配置！
```

---

## 4.6 解除/删除部署

### 步骤 4.6.1 禁用GitHub Pages

```
1. Settings > Pages
2. Source 选择 "None"
3. 点击 "Save"

网站立即下线！
```

### 步骤 4.6.2 删除仓库

```
⚠️ 这会永久删除所有代码和部署！

1. Settings > 最底部 "Danger Zone"
2. 点击 "Delete this repository"
3. 输入仓库名称确认
4. 点击 "Delete repository"
```

### 步骤 4.6.3 注销GitHub账号

```
⚠️ 会永久删除所有仓库和数据！

1. Settings > 左侧 "Account"
2. 滚到 "Danger Zone"
3. 点击 "Delete your account"
4. 选择原因（可选）
5. 输入密码
6. 点击 "Delete my account"
7. 确认所有警告

注意：
├── 如果你是组织唯一所有者，无法删除
├── 需要先转移或删除所有仓库
└── 删除后用户名无法恢复
```

---

## 4.7 防扣费详细措施

### ✅ GitHub Pages 天生安全

```
为什么最安全：

✅ 没有信用卡入口
✅ 没有付费选项
✅ 没有消费功能
✅ Public仓库永远免费
✅ 无限带宽
✅ 无限存储（软限制1GB）

唯一的"付费"是 Private 仓库（需升级）
但这不影响 Pages 的使用
```

### 4.7.1 安全设置

```
建议启用：

1. Two-factor authentication
   ├── Settings > Password and authentication
   ├── 启用 2FA
   └── 保护账号安全

2. 删除访问令牌
   ├── Settings > Developer settings
   ├── 检查 Personal access tokens
   └── 删除不需要的令牌
```

---

# 通用防扣费策略

## 核心原则

```
╔══════════════════════════════════════════════════════╗
║  防扣费黄金法则                                        ║
╠══════════════════════════════════════════════════════╣
║                                                      ║
║  1. 永远不绑定信用卡                                  ║
║     ↓                                                ║
║  2. 永远不填写支付信息                                ║
║     ↓                                                ║
║  3. 永远不相信"免费试用"                              ║
║     ↓                                                ║
║  4. 永远不在网站上点击升级按钮                         ║
║     ↓                                                ║
║  5. 定期检查账号状态                                  ║
║                                                      ║
╚══════════════════════════════════════════════════════╝
```

## 通用检查清单

### 每周检查

```
□ 登录各平台确认账号正常
□ 检查无异常邮件
□ 确认网站正常访问
```

### 每月检查

```
□ 进入 Billing 页面
□ 确认 Plan 是免费版
□ 确认无绑定的支付方式
□ 检查使用量是否在免费额度内
□ 确认无未知订阅
```

### 每季度检查

```
□ 检查邮箱中的账单/发票邮件
□ 确认无意外扣费
□ 更新密码（可选但推荐）
```

---

# 解除删除账号教程

## 各平台删除对比

| 平台 | 删除入口 | 需要验证码 | 可恢复 |
|------|----------|------------|--------|
| Vercel | Account > Danger Zone | 是 | 24小时内 |
| Netlify | Account > Delete | 是 | 否 |
| Cloudflare | Profile > Authentication | 是 | 否 |
| GitHub | Account > Danger Zone | 是（双重） | 否 |

## 详细删除步骤

### Vercel

```
1. 访问 https://vercel.com/account
2. 滚到最底部 "Account Deletion"
3. 点击 "Delete Account"
4. 输入当前密码
5. 点击 "Continue"
6. 去邮箱点击确认链接
7. 等待24小时自动删除
```

### Netlify

```
1. 访问 https://app.netlify.com
2. 点击头像 > User settings
3. 进入 "General"
4. 滚到最底部 "Account"
5. 点击 "Delete Netlify account"
6. 选择删除原因
7. 点击 "Delete my account"
8. 永久删除
```

### Cloudflare

```
1. 访问 https://dash.cloudflare.com
2. 点击头像 > Profile
3. 选择 "Authentication"
4. 滚到 "Make Deletion Request"
5. 点击 "Delete account"
6. 选择删除原因
7. 点击 "Delete Account"
8. 永久删除（影响所有CF服务）
```

### GitHub

```
1. 访问 https://github.com/settings/admin
2. 滚到 "Delete account"
3. 点击 "Delete your account"
4. 选择：
   - "Delete only the account"
   - 或 "Delete account and all organizations"
5. 输入仓库名称确认
6. 输入密码
7. 点击 "I understand, delete this account"
8. 永久删除
```

## 删除后检查

```
删除账号后，请确认：

□ 检查邮箱无扣费通知
□ 确认信用卡无异常交易
□ 检查域名解析是否已清除
□ 如果使用了自定义域名，考虑删除DNS记录
```

---

# 总结与推荐

## 选择建议

| 如果你 | 推荐使用 | 原因 |
|--------|----------|------|
| 追求最安全 | Cloudflare Pages | 无付费入口 |
| 需要框架支持 | Vercel | 自动检测框架 |
| 纯静态网站 | GitHub Pages | 最简单 |
| 需要CDN加速 | Cloudflare Pages | 全球CDN免费 |
| 预览功能需求 | Vercel/Netlify | PR预览强大 |

## 最终推荐

```
个人项目首选：
1. Cloudflare Pages - 最安全，无后顾之忧
2. Vercel - 功能最全，生态最好

团队项目：
1. Vercel - 协作功能完善
2. Netlify - 界面友好

静态网站：
1. GitHub Pages - 零成本，零配置
```

---

# 常见问题

## Q1: 免费部署有流量限制吗？

```
大多数平台都有免费流量额度：
- Vercel: 100GB/月
- Netlify: 100GB/月
- Cloudflare Pages: 无限
- GitHub Pages: 无限

对于个人项目来说，通常足够使用。
```

## Q2: 自定义域名需要付费吗？

```
域名本身需要付费（通常60-100元/年）

但以下平台免费提供域名解析和SSL证书：
- Vercel（免费）
- Netlify（免费）
- Cloudflare Pages（免费）
- GitHub Pages（免费）

你可以使用任何域名注册商购买域名。
```

## Q3: 网站打开速度如何？

```
取决于平台和地理位置：

- Vercel/Netlify: 全球CDN，在中国访问速度一般
- Cloudflare Pages: 全球CDN，包含中国节点，速度较快
- GitHub Pages: 主要服务器在美国，中国访问较慢

如果面向中国用户，建议使用Cloudflare Pages。
```

## Q4: 如何防止被恶意订阅？

```
1. 永远不要绑定信用卡
2. 忽略所有升级弹窗
3. 定期检查账号状态
4. 使用独立邮箱注册
5. 启用双因素认证（2FA）
```

---

> 文档更新日期：2026-03-28
> 如有问题，请检查各平台最新官方文档
