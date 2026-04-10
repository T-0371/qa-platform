# QA技术论坛平台 - 第五轮Bug修复任务分解

## 📌 任务总览
- **总任务数**: 5个主要问题 + 1个回归测试 = 6个大任务
- **预计子任务数**: 25-30个
- **执行模式**: 顺序执行（基于依赖关系）

---

## 🔴 任务1: 管理后台分页器重构 (问题1)

### 任务描述
将admin.html的用户管理和问题管理分页从"后端分页模式"完全重构为"前端分页模式"，参考questions.html的实现。

### 子任务列表

#### 1.1 添加全局数据存储变量
**优先级**: P0 | **预计时间**: 5分钟 | **依赖**: 无
**具体操作**:
- [ ] 在admin.html的`<script>`标签中（L1731附近）添加全局数组:
  ```javascript
  let allUsers = [];
  let allQuestionsData = [];
  let filteredUsers = [];
  let filteredQuestions = [];
  ```
**验收标准**: 变量声明成功，无语法错误

---

#### 1.2 重构loadUsers函数 - 改为全量加载
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 1.1
**具体操作**:
- [ ] 修改L1960-1993的`loadUsers`函数:
  - 移除page参数（或保留但忽略）
  - URL改为`?page=1&size=1000`
  - 将获取的数据存入`allUsers[]`和`filteredUsers[]`
  - 调用新的`displayUsers()`函数而非直接渲染
**代码位置**: admin.html L1960-1993
**验收标准**:
- [ ] 控制台日志显示: `allUsers长度: X, filteredUsers长度: X`
- [ ] 页面初次加载时显示前pageSize条数据

---

#### 1.3 新增displayUsers函数 - 本地切片渲染
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 1.2
**具体操作**:
- [ ] 在loadUsers函数后新增`displayUsers()`函数:
  ```javascript
  function displayUsers() {
      const startIndex = (usersPage - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      const pageUsers = filteredUsers.slice(startIndex, endIndex);

      renderUsersTable(pageUsers);
      renderPagination('usersPagination', filteredUsers.length, usersPage, (p) => {
          usersPage = p;
          displayUsers();
      });
  }
  ```
- [ ] 修改`renderUsersTable`函数，移除内部的startIndex计算（L2003）
**代码位置**: admin.html L1993后插入
**验收标准**:
- [ ] 表格显示的数据量为pageSize指定的数量
- [ ] 切换页码后数据显示正确

---

#### 1.4 修改changeUsersPageSize - 调用displayUsers
**优先级**: P0 | **预计时间**: 5分钟 | **依赖**: 1.3
**具体操作**:
- [ ] 修改L1708-1714的`changeUsersPageSize`函数:
  ```javascript
  function changeUsersPageSize(newSize) {
      console.log('🔄 切换用户管理每页条数:', newSize);
      pageSize = parseInt(newSize);
      usersPage = 1;
      displayUsers(); // 关键改动：不再调用loadUsers
  }
  ```
**验收标准**:
- [ ] 选择"每页5条"→ 表格立即显示5行
- [ ] 控制台无错误信息

---

#### 1.5 重构loadQuestions函数 - 同样改为全量加载
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 1.1
**具体操作**:
- [ ] 修改L2126-2158的`loadQuestions`函数，应用与loadUsers相同的模式
- [ ] 新增`displayQuestions()`函数
- [ ] 数据存入`allQuestionsData[]`和`filteredQuestions[]`
**代码位置**: admin.html L2126-2158
**验收标准**: 问题管理模块的分页功能正常

---

#### 1.6 修改changeQuestionsPageSize - 调用displayQuestions
**优先级**: P0 | **预计时间**: 5分钟 | **依赖**: 1.5
**具体操作**:
- [ ] 修改L1716-1724的`changeQuestionsPageSize`函数
- [ ] 改为调用`displayQuestions()`而非`loadQuestions`
**验收标准**: 问题管理的每页条数切换正常工作

---

#### 1.7 修复搜索功能兼容新分页模式
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 1.3, 1.5
**具体操作**:
- [ ] 修改`searchUsers()`函数(L2025-2029):
  - 搜索时过滤`allUsers[]`到`filteredUsers[]`
  - 重置`usersPage = 1`
  - 调用`displayUsers()`
- [ ] 修改`searchQuestions()`/搜索相关函数:
  - 应用相同逻辑
**验收标准**:
- [ ] 搜索"admin"后只显示匹配用户
- [ ] 搜索结果的分页功能正常

---

#### 1.8 测试验证 - 用户管理分页
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 1.4, 1.7
**测试步骤**:
1. 打开管理后台 → 用户管理tab
2. 默认显示10条数据 ✅
3. 切换到"每页5条" → 应显示5行 ✅
4. 切换到"每页20条" → 应显示20行（或全部如果<20）✅
5. 点击第2页 → 显示下一批数据 ✅
6. 输入关键词搜索 → 结果正确分页 ✅
7. 检查控制台无JS错误 ✅

**Bug记录格式**:
```
时间: HH:MM:SS
现象: xxx
预期: xxx
实际: xxx
截图: (如有)
```

---

## 🔴 任务2: 前端分页器统一样式 (问题2)

### 任务描述
检查并统一所有前端页面（除admin.html和questions.html外）的分页器样式和逻辑。

### 子任务列表

#### 2.1 扫描所有前端页面中的分页实现
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 无
**具体操作**:
- [ ] 使用Grep工具搜索所有HTML文件中的`pagination`、`updatePagination`、`renderPagination`关键字
- [ ] 列出需要修改的页面清单:
  - [ ] tags.html (如果有)
  - [ ] home.html (如果有)
  - [ ] question-detail.html (如果有)
  - [ ] 其他页面
**输出**: 分页器使用情况报告

---

#### 2.2 对每个页面应用questions.html标准
**优先级**: P0 | **预计时间**: 每个页面10分钟 | **依赖**: 2.1
**对每个页面执行**:
- [ ] 替换HTML结构为标准格式（L1239-1253参考）
- [ ] 复制CSS样式（从questions.css）
- [ ] 重写JavaScript逻辑（采用updatePagination模式）
- [ ] 测试分页功能

**验收标准**: 所有页面分页器外观和行为一致

---

## 🔴 任务3: 个人中心分页器完善 (问题3)

### 任务描述
修复profile.html中"我的问题"和"我的回答"的分页器，使其完整显示并功能正常。

### 子任务列表

#### 3.1 修改HTML结构 - 添加标准分页组件
**优先级**: P0 | **预计时间**: 5分钟 | **依赖**: 无
**具体操作**:
- [ ] 修改L1349的`questionsPagination` div:
  ```html
  <div id="questionsPagination" class="pagination">
      <div class="pagination-info">
          共 <span id="questionsTotalCount">0</span> 条，
          第 <span id="questionsCurrentPage">1</span> / <span id="questionsTotalPages">1</span> 页
      </div>
      <div class="page-size-selector">
          <label for="questionsPageSize">每页显示：</label>
          <select id="questionsPageSize" onchange="changeProfileQuestionsPageSize(this.value)">
              <option value="5">5条</option>
              <option value="10" selected>10条</option>
              <option value="20">20条</option>
              <option value="50">50条</option>
          </select>
      </div>
  </div>
  ```
- [ ] 对answersPagination做同样修改（L1356）
**代码位置**: profile.html L1349, L1356
**验收标准**: HTML结构符合questions.html标准

---

#### 3.2 添加CSS样式
**优先级**: P0 | **预计时间**: 5分钟 | **依赖**: 3.1
**具体操作**:
- [ ] 在profile.html的`<style>`标签末尾添加完整的pagination CSS（从questions.css复制）
- [ ] 确保不与现有样式冲突
**代码位置**: profile.html `<style>`标签内
**验收标准**: 分页器可见且样式正确

---

#### 3.3 重写updateProfilePagination函数
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 3.1, 3.2
**具体操作**:
- [ ] 删除旧的`updateProfilePagination`函数(L1735-1799)
- [ ] 新增两个专用函数:
  ```javascript
  function updateQuestionsPagination() {
      // 与questions.html的updatePagination完全一致
      const paginationDiv = document.getElementById('questionsPagination');
      const totalCount = myQuestions.length;
      const totalPages = Math.ceil(totalCount / pageSize) || 1;

      document.getElementById('questionsTotalCount').textContent = totalCount;
      document.getElementById('questionsCurrentPage').textContent = questionsCurrentPage;
      document.getElementById('questionsTotalPages').textContent = totalPages;

      // 清除旧按钮，创建新按钮...
  }

  function updateAnswersPagination() {
      // 类似逻辑
  }
  ```
- [ ] 新增`changeProfileQuestionsPageSize`函数:
  ```javascript
  function changeProfileQuestionsPageSize(newSize) {
      pageSize = parseInt(newSize);
      questionsCurrentPage = 1;
      displayMyQuestions();
  }
  ```
**代码位置**: profile.html L1735-1799区域
**验收标准**:
- [ ] "我的问题"下方显示完整分页器
- [ ] 统计信息和按钮都正确显示

---

#### 3.4 测试验证
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 3.3
**测试步骤**:
1. 登录 → 进入个人中心 → 点击"我的问题"
2. 验证分页器完整显示 ✅
3. 切换每页条数 → 数据量变化 ✅
4. 点击页码 → 正确跳转 ✅
5. 点击"我的回答" → 分页器同样正常 ✅

---

## 🔴 任务4: 聊天状态显示修复 (问题4)

### 任务描述
修复聊天页面中用户切换聊天对象后状态误显示为"离线"的问题。

### 子任务列表

#### 4.1 分析sendUserStatus调用场景
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 无
**具体操作**:
- [ ] 在chat.html中搜索所有`sendUserStatus`调用点:
  - L1254: 选择聊天对象时
  - L1784: 加载用户列表时
  - L2354: 页面卸载时
- [ ] 记录每个调用点的参数值
**输出**: sendUserStatus调用场景分析表

---

#### 4.2 修改状态消息接收逻辑
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 4.1
**具体操作**:
- [ ] 修改L1378-1389的状态消息处理:
  ```javascript
  // 原代码
  } else if (statusMessage.status === 'OFFLINE' || statusMessage.status === 'AWAY') {
      userChatStatus.delete(statusMessage.userId);
  }

  // 修改为
  } else if (statusMessage.status === 'OFFLINE') {
      // 只有明确离线时才删除聊天状态
      userChatStatus.delete(statusMessage.userId);
  }
  // AWAY状态时不删除，因为用户可能只是暂时离开屏幕
  ```
**代码位置**: chat.html L1384-1386
**验收标准**: 切换聊天对象时不触发离线状态

---

#### 4.3 修改UI显示逻辑 - 增加防御性判断
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 4.2
**具体操作**:
- [ ] 修改L1419-1425的else分支:
  ```javascript
  // 原代码
  if (onlineStatus === 'ONLINE') {
      newStatusText = '在线';
  } else {
      newStatusText = '离线'; // ❌
  }

  // 修改为
  if (onlineStatus === 'ONLINE') {
      newStatusText = '在线';
      newStatusClass = 'chat-status online';
  } else if (onlineStatus === 'AWAY') {
      newStatusText = '离开';
      newStatusClass = 'chat-status away';
  } else if (!onlineStatus) {
      // 从未收到过状态消息，默认在线（友好体验）
      newStatusText = '在线';
      newStatusClass = 'chat-status online';
  } else {
      newStatusText = '离线';
      newStatusClass = 'chat-status offline';
  }
  ```
**代码位置**: chat.html L1419-1425
**验收标准**: 未知状态默认显示"在线"

---

#### 4.4 测试验证 - 多用户聊天场景
**优先级**: P0 | **预计时间**: 20分钟 | **依赖**: 4.3
**测试步骤**（需2个浏览器/用户）:
1. 浏览器A: 用户1登录 → 与用户2聊天 → 看到"在线" ✅
2. 浏览器B: 用户2登录 → 与用户1聊天 → 看到"正在与您聊天" ✅
3. 浏览器B: 用户2切换到与用户3聊天
4. 浏览器A: 检查用户2状态 → 应显示"在线"（非"离线"）✅
5. 浏览器B: 用户2关闭浏览器
6. 浏览器A: 检查用户2状态 → 应显示"离线" ✅

**记录结果**: 通过/失败 + 截图

---

## 🔴 任务5: 消息通知系统修复 (问题5)

### 任务描述
修复导航栏通知badge未更新和通知列表未过滤实时聊天消息的问题。

### 子任务列表

#### 5.1 检查后端通知API
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 无
**具体操作**:
- [ ] 定位NotificationController.java
- [ ] 查找`/notifications/unread/count`接口实现
- [ ] 检查SQL查询是否包含CHAT类型:
  ```sql
  -- 可能的查询
  SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = 0 AND type IN ('LIKE', 'COMMENT', 'ANSWER')
  -- 应该改为包含 'CHAT', 'CHAT_MESSAGE'
  ```
- [ ] 如果缺少CHAT类型，添加到查询条件
**涉及文件**: 后端Java文件（路径待查找）
**输出**: 后端API分析报告

---

#### 5.2 增强前端Badge更新逻辑
**优先级**: P0 | **预计时间**: 10分钟 | **依赖**: 5.1
**具体操作**:
- [ ] 修改common.js L461-517的`updateNotificationBadge`函数:
  - 增加详细console.log
  - 强制设置badge样式（防止被覆盖）
  - 增加错误处理
- [ ] 确保在common.js初始化时调用一次
- [ ] 添加setInterval定时器（30秒间隔）
**代码位置**: common.js L461-517
**验收标准**:
- [ ] 有未读消息时badge显示红色数字
- [ ] console.log输出: `🔔 未读通知数: X`

---

#### 5.3 实现智能过滤函数
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 无
**具体操作**:
- [ ] 在common.js中新增`shouldFilterChatNotification`函数(L639前):
  ```javascript
  function shouldFilterChatNotification(notification, activeChatUserId) {
      if (!activeChatUserId) return false; // 无活跃聊天，不过滤
      if (notification.fromUserId.toString() !== activeChatUserId.toString()) return false; // 不是当前聊天对象，不过滤

      var chatOpenTime = localStorage.getItem('chatOpenTime_' + activeChatUserId);
      if (chatOpenTime) {
          var msgTime = new Date(notification.createdAt).getTime();
          var openTime = new Date(chatOpenTime).getTime();
          if (msgTime < openTime) return false; // 历史消息，不过滤
      }

      return true; // 过滤实时消息
  }
  ```

- [ ] 新增`getActiveChatUserId`函数:
  ```javascript
  function getActiveChatUserId() {
      var chatUserSelect = document.getElementById('chatUserSelect');
      if (chatUserSelect && chatUserSelect.value) return chatUserSelect.value;

      var urlParams = new URLSearchParams(window.location.search);
      if (urlParams.has('userId')) return urlParams.get('userId');

      return localStorage.getItem('lastChatUserId') || null;
  }
  ```
**代码位置**: common.js L639之前
**验收标准**: 函数定义成功，无语法错误

---

#### 5.4 重写renderNotifications函数
**优先级**: P0 | **预计时间**: 15分钟 | **依赖**: 5.3
**具体操作**:
- [ ] 修改common.js L639-682的`renderNotifications`:
  - 开头调用`getActiveChatUserId()`
  - 在forEach循环中使用`shouldFilterChatNotification`判断
  - 统计真实未读数（用于同步更新badge）
**关键改动**:
```javascript
function renderNotifications(notifications) {
    var currentActiveChatUserId = getActiveChatUserId(); // 新增
    var html = '';
    var realUnreadCount = 0;

    notifications.forEach(function(notification) {
        var isChatMsg = (notification.type === 'CHAT' || ...);
        if (isChatMsg && notification.fromUserId) {
            if (shouldFilterChatNotification(notification, currentActiveChatUserId)) {
                console.log('🔄 过滤实时聊天消息:', notification.id);
                return;
            }
        }

        if (!notification.isRead) realUnreadCount++; // 统计实际未读数
        // 渲染...
    });
}
```
**代码位置**: common.js L639-682
**验收标准**:
- [ ] 当前聊天对象的实时消息不在列表中显示
- [ ] 其他用户的聊天消息正常显示

---

#### 5.5 记录聊天窗口打开时间
**优先级**: P0 | **预计时间**: 5分钟 | **依赖**: 无
**具体操作**:
- [ ] 在chat.html的`selectUser`函数中添加:
  ```javascript
  localStorage.setItem('chatOpenTime_' + userId, new Date().toISOString());
  localStorage.setItem('lastChatUserId', userId);
  ```
- [ ] 在页面初始化时也记录初始聊天对象的时间
**代码位置**: chat.html selectUser函数
**验收标准**: localStorage中有chatOpenTime记录

---

#### 5.6 测试验证 - 通知系统
**优先级**: P0 | **预计时间**: 20分钟 | **依赖**: 5.2, 5.4, 5.5
**测试步骤**:
1. 用户A给当前用户发送消息
2. 导航栏badge显示数字+1 ✅
3. 打开通知面板 → 显示A的消息 ✅
4. 打开与A的聊天窗口
5. A再发一条消息
6. 打开通知面板 → 不显示第二条消息（已过滤）✅
7. 切换到与其他用户的聊天
8. A再发消息
9. 打开通知面板 → 显示第三条消息 ✅

**记录**: 通过/失败 + 控制台日志截图

---

## 🔄 任务6: 全项目回归测试

### 任务描述
完成所有修复后进行全项目回归测试，确保无遗留BUG。

### 子任务列表

#### 6.1 功能回归测试矩阵执行
**优先级**: P0 | **预计时间**: 60分钟 | **依赖**: 任务1-5全部完成
**具体操作**:
按照spec.md中的测试矩阵逐项测试:

| # | 模块 | 测试项 | 结果 |
|---|------|--------|------|
| 1 | 管理后台-用户管理 | 每页5条/10条/20条/50条 | ⬜ |
| 2 | 管理后台-用户管理 | 页码跳转 | ⬜ |
| 3 | 管理后台-用户管理 | 搜索后分页 | ⬜ |
| 4 | 管理后台-问题管理 | 每页条数切换 | ⬜ |
| 5 | 前端-问题列表页 | 分页器样式一致性 | ⬜ |
| 6 | 前端-其他页面 | 分页功能 | ⬜ |
| 7 | 个人中心-我的问题 | 分页器完整性 | ⬜ |
| 8 | 个人中心-我的问题 | 条数切换 | ⬜ |
| 9 | 聊天-状态显示 | 切换后仍在线 | ⬜ |
| 10 | 聊天-状态显示 | 真正离线 | ⬜ |
| 11 | 通知-Badge | 未读数字显示 | ⬜ |
| 12 | 通知-列表 | 过滤实时消息 | ⬜ |

**通过标准**: 全部12项通过 ✅

---

#### 6.2 兼容性测试
**优先级**: P1 | **预计时间**: 30分钟 | **依赖**: 6.1
**具体操作**:
- [ ] Chrome最新版测试核心流程
- [ ] Firefox最新版测试核心流程
- [ ] Edge最新版测试核心流程
- [ ] 手机浏览器测试响应式布局

---

#### 6.3 异常场景测试
**优先级**: P1 | **预计时间**: 20分钟 | **依赖**: 6.1
**具体操作**:
- [ ] 断网时分页操作（应显示错误提示）
- [ ] 快速连续点击页码（应防抖处理）
- [ ] 空数据状态显示（应显示"暂无数据"）
- [ ] 并发多用户聊天状态更新

---

#### 6.4 性能测试
**优先级**: P2 | **预计时间**: 15分钟 | **依赖**: 6.1
**具体操作**:
- [ ] 管理后台加载1000+用户时页面响应时间 < 2秒
- [ ] 聊天WebSocket消息处理无延迟感
- [ ] 通知Badge更新不影响页面性能

---

#### 6.5 问题汇总与二次修复
**优先级**: P0 | **预计时间**: 视发现的问题数量而定 | **依赖**: 6.1-6.4
**具体操作**:
- [ ] 汇总所有测试中发现的问题
- [ ] 按优先级排序（P0 > P1 > P2）
- [ ] 逐一修复
- [ ] 修复后重新测试对应项
- [ ] 循环直到零BUG

**退出条件**:
- ✅ 所有P0问题已修复
- ✅ 所有P1问题已修复或记录延期
- ✅ 核心功能100%可用
- ✅ 用户确认验收

---

## 📊 进度跟踪表

| 任务ID | 任务名称 | 状态 | 开始时间 | 完成时间 | 实际耗时 | 备注 |
|--------|----------|------|----------|----------|----------|------|
| 1.1 | 添加全局变量 | ⬜ | - | - | - | - |
| 1.2 | 重构loadUsers | ⬜ | - | - | - | - |
| 1.3 | 新增displayUsers | ⬜ | - | - | - | - |
| 1.4 | 修改changeUsersPageSize | ⬜ | - | - | - | - |
| 1.5 | 重构loadQuestions | ⬜ | - | - | - | - |
| 1.6 | 修改changeQuestionsPageSize | ⬜ | - | - | - | - |
| 1.7 | 修复搜索功能 | ⬜ | - | - | - | - |
| 1.8 | 测试用户管理分页 | ⬜ | - | - | - | - |
| 2.1 | 扫描前端分页实现 | ⬜ | - | - | - | - |
| 2.2 | 统一分页器样式 | ⬜ | - | - | - | - |
| 3.1 | 修改HTML结构 | ⬜ | - | - | - | - |
| 3.2 | 添加CSS样式 | ⬜ | - | - | - | - |
| 3.3 | 重写分页函数 | ⬜ | - | - | - | - |
| 3.4 | 测试个人中心分页 | ⬜ | - | - | - | - |
| 4.1 | 分析状态消息场景 | ⬜ | - | - | - | - |
| 4.2 | 修改接收逻辑 | ⬜ | - | - | - | - |
| 4.3 | 修改UI显示逻辑 | ⬜ | - | - | - | - |
| 4.4 | 测试聊天状态 | ⬜ | - | - | - | - |
| 5.1 | 检查后端API | ⬜ | - | - | - | - |
| 5.2 | 增强Badge逻辑 | ⬜ | - | - | - | - |
| 5.3 | 实现过滤函数 | ⬜ | - | - | - | - |
| 5.4 | 重写renderNotifications | ⬜ | - | - | - | - |
| 5.5 | 记录打开时间 | ⬜ | - | - | - | - |
| 5.6 | 测试通知系统 | ⬜ | - | - | - | - |
| 6.1 | 功能回归测试 | ⬜ | - | - | - | - |
| 6.2 | 兼容性测试 | ⬜ | - | - | - | - |
| 6.3 | 异常场景测试 | ⬜ | - | - | - | - |
| 6.4 | 性能测试 | ⬜ | - | - | - | - |
| 6.5 | 问题汇总修复 | ⬜ | - | - | - | - |

---

## ⚠️ 风险提示

### 高风险任务
1. **任务1.2-1.3** (admin分页重构): 影响面广，可能破坏现有功能
   - 缓解措施: 每步完成后立即测试
2. **任务4.2-4.3** (聊天状态): 涉及WebSocket消息流
   - 缓解措施: 需要双浏览器测试环境
3. **任务5.1** (后端API): 可能需要修改Java代码
   - 缓解措施: 先分析再动手，必要时寻求确认

### 依赖关系图
```
任务1.1 ──→ 任务1.2 ──→ 任务1.3 ──→ 任务1.4 ─┐
                                              ├──→ 任务1.8
任务1.1 ──→ 任务1.5 ──→ 任务1.6 ──────────────┤
                                              │
任务1.3 + 任务1.5 ──→ 任务1.7 ────────────────┘

任务2.1 ──→ 任务2.2

任务3.1 ──→ 任务3.2 ──→ 任务3.3 ──→ 任务3.4

任务4.1 ──→ 任务4.2 ──→ 任务4.3 ──→ 任务4.4

任务5.1 ──→ 任务5.2
任务5.3 ──→ 任务5.4 ← 任务5.5
                    ├──→ 任务5.6

任务1-5 全部完成 ──→ 任务6.1 ──→ 任务6.2-6.4 ──→ 任务6.5
```

---

**文档版本**: v5.0
**创建时间**: 2026-04-10
**最后更新**: 2026-04-10
**负责人**: AI Assistant
**审核人**: User (待确认)
