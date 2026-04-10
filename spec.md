# QA技术论坛平台 - 第五轮Bug修复规范文档

## 📋 项目概述
- **项目路径**: `e:\Test\XiangMu\LunTan_Demo\qa-platform`
- **技术栈**: Spring Boot 2.7.15 + MyBatis-Plus + 静态HTML/CSS/JS前端
- **修复轮次**: 第5轮（前4轮均未完全解决问题）
- **修复优先级**: 全部为P0（必须100%完成）

---

## 🔴 问题1: 管理后台分页器 - 选择条数后数据不变化 + 页码错误

### 问题描述
用户在管理后台(`admin.html`)的用户管理/问题管理模块中选择"每页显示条数"(5/10/20/50)后：
1. **核心BUG**: 页码数字发生了变化，但表格中的数据行数完全没有改变
2. **次要BUG**: 页码显示在选择条数后存在错误（如显示"共12条"但实际应该根据新条数重新计算）

### 根因分析
通过代码审查发现以下问题：

#### 问题A: 后端分页 vs 前端分页模式冲突
- **参考标准** (`questions.html` L1270-1310): 采用**前端全量加载+本地切片**模式
  ```javascript
  // questions.html工作流程:
  1. loadQuestions() 一次性获取全部数据 → 存入 allQuestions[]
  2. displayQuestions() 用 slice(startIndex, endIndex) 切片
  3. 切换pageSize时只重算切片，不再请求后端
  ```
- **当前实现** (`admin.html` L1960-1993): 采用**后端分页**模式
  ```javascript
  // admin.html当前流程:
  1. loadUsers(page, keyword) 每次请求后端 ?size=pageSize
  2. 直接渲染后端返回的 records[]
  3. 切换pageSize时重新请求后端
  ```

#### 问题B: API返回数据的total字段不可靠
```javascript
// admin.html L1976-1982
if (result.records && result.records.length > 0 && (!result.total || result.total === 0)) {
    total = result.records.length; // fallback到当前页记录数
}
```
- 当后端返回 `{total: 0, records: Array(12)}` 时，total被设为12
- 但这是**当前页的记录数**，不是数据库总记录数
- 导致页码计算基于错误的total值

#### 问题C: renderPagination函数的total参数来源错误
```javascript
// admin.html L1985
renderPagination('usersPagination', total, page, callback);
// 这里的total是从API响应中提取的，可能不准确
```

### 修复方案
**采用questions.html的前端分页模式重构admin.html**:

#### 步骤1: 重构数据加载逻辑
```javascript
// 新增全局数组存储全部数据
let allUsers = [];
let allQuestions = [];
let filteredUsers = [];
let filteredQuestions = [];

// 修改loadUsers - 一次获取全部数据
async function loadUsers(keyword = '') {
    let url = API_BASE_URL + '/users?page=1&size=1000'; // 获取全部
    if (keyword) url += '&keyword=' + encodeURIComponent(keyword);

    const res = await fetch(url);
    const data = await res.json();

    if (data.code === 200) {
        const result = data.data;
        allUsers = Array.isArray(result) ? result : (result.records || []);
        filteredUsers = [...allUsers];
        displayUsers(); // 调用新的display函数
    }
}
```

#### 步骤2: 新增displayUsers函数（本地切片）
```javascript
function displayUsers() {
    const startIndex = (usersPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const pageUsers = filteredUsers.slice(startIndex, endIndex);

    renderUsersTable(pageUsers); // 渲染当前页数据
    renderPagination('usersPagination', filteredUsers.length, usersPage, (p) => {
        usersPage = p;
        displayUsers();
    });
}
```

#### 步骤3: 修改changeUsersPageSize
```javascript
function changeUsersPageSize(newSize) {
    pageSize = parseInt(newSize);
    usersPage = 1;
    displayUsers(); // 不再请求后端，直接重新切片显示
}
```

#### 步骤4: 同样重构问题管理的分页逻辑
对`loadQuestions`、`displayQuestions`、`changeQuestionsPageSize`应用相同模式

### 验收标准
- [ ] 选择"每页5条"→ 表格只显示5行数据
- [ ] 选择"每页20条"→ 表格显示20行数据（如果总数>=20）
- [ ] 页码显示正确："共 X 条，第 Y / Z 页"
- [ ] 点击页码按钮正常跳转
- [ ] 搜索功能正常工作且不影响分页

### 涉及文件
- `src/main/resources/static/admin.html`
  - L1654-1706: renderPagination函数
  - L1708-1724: changeUsersPageSize/changeQuestionsPageSize
  - L1731-1733: 状态变量声明
  - L1960-1993: loadUsers函数
  - L1995-2023: renderUsersTable函数
  - L2126-2158: loadQuestions函数（需同样修改）

---

## 🔴 问题2: 前端分页器样式和逻辑 - 未参考questions.html

### 问题描述
前端页面（如标签页面等）的分页器样式和行为逻辑没有按照`questions.html`的标准实现。

### 参考标准 (questions.html L1364-1424)
```javascript
function updatePagination() {
    // 1. 更新统计信息span元素
    document.getElementById('totalCount').textContent = totalCount;
    document.getElementById('currentPageNum').textContent = currentPage;
    document.getElementById('totalPageNum').textContent = totalPages;

    // 2. 清除旧按钮
    const existingButtons = paginationDiv.querySelectorAll('button');
    existingButtons.forEach(btn => btn.remove());

    // 3. 创建上一页按钮
    const prevButton = document.createElement('button');
    prevButton.textContent = '上一页';
    // ... 设置disabled和onclick

    // 4. 创建页码按钮（带闭包）
    for (let i = 1; i <= totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.onclick = (function(page) {
            return function() { callback(page); };
        })(i);
    }

    // 5. 创建下一页按钮
    // ...
}
```

### HTML结构标准 (questions.html L1239-1253)
```html
<div id="pagination" class="pagination">
    <div class="pagination-info">
        共 <span id="totalCount">0</span> 条，
        第 <span id="currentPageNum">1</span> / <span id="totalPageNum">1</span> 页
    </div>
    <div class="page-size-selector">
        <label for="pageSize">每页显示：</label>
        <select id="pageSize" onchange="changePageSize(this.value)">
            <option value="5">5条</option>
            <option value="10" selected>10条</option>
            <option value="20">20条</option>
            <option value="50">50条</option>
        </select>
    </div>
</div>
```

### CSS样式标准 (questions.css)
```css
.pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: nowrap; /* 关键：不允许换行 */
    padding: 16px 20px;
    gap: 20px;
}

.pagination-info {
    color: #64748b;
    font-size: 14px;
    white-space: nowrap; /* 单行显示 */
}

.page-size-selector {
    display: flex;
    align-items: center;
    gap: 8px;
}

.page-size-selector select {
    padding: 6px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    background: white;
}

.pagination button {
    padding: 6px 14px;
    border: 1px solid #e2e8f0;
    background: white;
    cursor: pointer;
    border-radius: 6px;
    transition: all 0.2s;
}

.pagination button:hover:not(:disabled) {
    background: #f1f5f9;
    border-color: #94a3b8;
}

.pagination button.active {
    background: #6366f1;
    color: white;
    border-color: #6366f1;
}

.pagination button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
```

### 修复方案
1. **定位所有前端页面中使用分页器的位置**（tags.html等）
2. **统一替换为questions.html的HTML结构**
3. **复制questions.css中的pagination相关样式**
4. **采用相同的JavaScript逻辑**（updatePagination函数）
5. **确保事件绑定使用addEventListener而非inline onclick**

### 验收标准
- [ ] 分页器样式与questions.html完全一致
- [ ] 选择每页条数后立即生效（无需刷新页面）
- [ ] 页码按钮点击正常，高亮当前页
- [ ] 上一页/下一页按钮禁用状态正确
- [ ] 统计信息实时更新

### 涉及文件
- 需要检查并修改的前端页面列表（待执行时确定）

---

## 🔴 问题3: 个人中心"我的问题"添加完整分页器

### 问题描述
个人中心页面(`profile.html`)点击"我的问题"tab后：
1. 数据已成功加载并显示
2. 底部出现了分页区域`<div id="questionsPagination">`，但**没有显示详细信息**
   - 当前实现(L1735-1799): updateProfilePagination函数会生成内容
   - 但可能因为CSS样式缺失或DOM操作时机问题导致不可见

### 当前代码分析
```javascript
// profile.html L1349
<div id="questionsPagination" class="pagination"></div>

// profile.html L1535-1557 - displayMyQuestions()
function displayMyQuestions() {
    var startIndex = (questionsCurrentPage - 1) * pageSize;
    var endIndex = startIndex + pageSize;
    var pageQuestions = myQuestions.slice(startIndex, endIndex);
    // ... 渲染pageQuestions
    updateProfilePagination('questions', myQuestions, questionsCurrentPage, 'questionsPagination');
}

// profile.html L1735-1799 - updateProfilePagination()
function updateProfilePagination(type, data, currentPage, paginationId) {
    var paginationDiv = document.getElementById(paginationId);
    paginationDiv.innerHTML = ''; // 清空
    // 创建 pagination-info div
    // 创建上一页/页码/下一页按钮
    // 使用闭包绑定onclick事件
}
```

### 发现的问题
1. **缺少CSS样式**: profile.html可能缺少pagination相关的CSS类定义
2. **HTML结构不符合标准**: 当前的updateProfilePagination动态生成的结构与questions.html不一致
3. **缺少page-size-selector**: 没有"每页显示"下拉框

### 修复方案
#### 方案A: 完全采用questions.html标准（推荐）
将profile.html的分页部分完全重写为与questions.html一致的实现：

**步骤1: 修改HTML结构** (L1349)
```html
<!-- 替换原有的简单div -->
<div id="questionsPagination" class="pagination">
    <div class="pagination-info">
        共 <span id="questionsTotalCount">0</span> 条，
        第 <span id="questionsCurrentPage">1</span> / <span id="questionsTotalPages">1</span> 页
    </div>
    <div class="page-size-selector">
        <label for="questionsPageSize">每页显示：</label>
        <select id="questionsPageSize" onchange="changeQuestionsPageSize(this.value)">
            <option value="5">5条</option>
            <option value="10" selected>10条</option>
            <option value="20">20条</option>
            <option value="50">50条</option>
        </select>
    </div>
</div>
```

**步骤2: 添加CSS样式** (从questions.css复制)
```css
/* 在profile.html的<style>标签中添加 */
.pagination { /* 复制questions.css的完整样式 */ }
```

**步骤3: 重写JavaScript逻辑**
```javascript
// 删除旧的updateProfilePagination函数
// 改用与questions.html完全一致的updatePagination逻辑
function updateQuestionsPagination() {
    const paginationDiv = document.getElementById('questionsPagination');
    const totalCount = myQuestions.length;
    const totalPages = Math.ceil(totalCount / pageSize) || 1;

    // 更新span元素
    document.getElementById('questionsTotalCount').textContent = totalCount;
    document.getElementById('questionsCurrentPage').textContent = questionsCurrentPage;
    document.getElementById('questionsTotalPages').textContent = totalPages;

    // 清除旧按钮，创建新按钮（与questions.html完全一致）
}

function changeQuestionsPageSize(newSize) {
    pageSize = parseInt(newSize);
    questionsCurrentPage = 1;
    displayMyQuestions();
}
```

### 验收标准
- [ ] "我的问题"下方显示完整的分页器
- [ ] 统计信息正确显示："共 X 条，第 Y / Z 页"
- [ ] "每页显示"下拉框可选5/10/20/50条
- [ ] 选择条数后立即更新显示的数据量
- [ ] 页码按钮可点击，当前页高亮
- [ ] 上一页/下一页按钮正常工作
- [ ] "我的回答"分页器同样修复（如存在）

### 涉及文件
- `src/main/resources/static/profile.html`
  - L1345-1350: HTML结构
  - L1372-1374: 状态变量
  - L1535-1557: displayMyQuestions函数
  - L1735-1799: updateProfilePagination函数（需重写）

---

## 🔴 问题4: 聊天状态切换后显示错误 - 应显示"在线"非"离线"

### 问题描述
聊天场景：
- 用户A与用户B正在聊天
- 用户B切换到与用户C聊天
- **预期**: A的聊天页面中B的状态应显示"在线"（因为B仍然在线，只是换了聊天对象）
- **实际**: A的聊天页面中B的状态变成了"离线"

### 当前代码分析
```javascript
// chat.html L1378-1389 - 接收状态消息
userOnlineStatus.set(statusMessage.userId, statusMessage.status);

if (statusMessage.chatWithUserId) {
    userChatStatus.set(statusMessage.userId, statusMessage.chatWithUserId);
} else if (statusMessage.status === 'OFFLINE' || statusMessage.status === 'AWAY') {
    userChatStatus.delete(statusMessage.userId); // ⚠️ 这里删除了聊天状态
}

// chat.html L1393-1426 - updateUserStatusUI()
const chatWithUser = userChatStatus.get(selectedUserId);
if (chatWithUser && chatWithUser == currentUser.id) {
    newStatusText = '正在与您聊天'; // 对方正在与自己聊天
} else {
    const onlineStatus = userOnlineStatus.get(selectedUserId);
    if (onlineStatus === 'ONLINE') {
        newStatusText = '在线';
    } else {
        newStatusText = '离线'; // ❌ 错误地显示离线
    }
}
```

### 根因分析
**核心问题**: 当B切换聊天对象时，系统向所有连接的用户广播状态消息：

**场景重现**:
1. B与A聊天时，发送状态: `{userId: B, status: 'ONLINE', chatWithUserId: A}`
2. A收到后: `userOnlineStatus.set(B, 'ONLINE')`, `userChatStatus.set(B, A)` ✅
3. B切换到C聊天时，发送状态: `{userId: B, status: 'ONLINE', chatWithUserId: C}`
4. A收到后: `userOnlineStatus.set(B, 'ONLINE')`, `userChatStatus.set(B, C)` ✅
5. A的UI更新: `chatWithUser = C`, `C != A` → 进入else分支
6. 检查`onlineStatus = 'ONLINE'` → 应该显示"在线" ✅

**但如果第4步的消息格式不同呢？**

假设B切换时发送的是:
```javascript
{userId: B, status: 'AWAY', chatWithUserId: null} // 或者不包含chatWithUserId
```
那么:
- `userChatStatus.delete(B)` → 删除了B的聊天状态
- `userOnlineStatus.set(B, 'AWAY')` → 状态变为离开
- UI显示: "离线" ❌

### 修复方案
#### 方案1: 修改状态消息发送逻辑（后端/前端发送端）【推荐】
确保切换聊天对象时发送正确的状态消息:

**chat.html L1254附近 - sendUserStatus函数**
```javascript
function sendUserStatus(status, chatWithUserId) {
    // 切换聊天对象时，不应该发送OFFLINE/AWAY
    // 应该继续发送ONLINE，只是更新chatWithUserId
    const statusMessage = {
        userId: currentUser.id,
        status: 'ONLINE', // 始终是ONLINE，除非真正离线
        chatWithUserId: chatWithUserId,
        timestamp: new Date().toISOString()
    };
    // 发送状态消息...
}
```

#### 方案2: 修改状态消息接收逻辑（前端接收端）
在接收到状态消息时增加智能判断:

```javascript
// chat.html L1378-1389 修改版
function handleStatusMessage(statusMessage) {
    const userId = statusMessage.userId;
    const status = statusMessage.status;
    const chatWith = statusMessage.chatWithUserId;

    // 始终更新在线状态
    userOnlineStatus.set(userId, status);

    // 只有在真正离线/离开时才清除聊天状态
    // 如果只是切换聊天对象（status仍是ONLINE），保留chatWithUserId
    if (chatWith) {
        userChatStatus.set(userId, chatWith);
    } else if (status === 'OFFLINE') {
        // 只有明确离线时才删除
        userChatStatus.delete(userId);
    }
    // AWAY状态时不删除chatWithUserId，因为用户可能只是暂时离开

    updateUserStatusUI();
}
```

#### 方案3: 修改UI显示逻辑（防御性编程）
即使状态消息有问题，也应该合理显示:

```javascript
// chat.html L1412-1426 修改版
const chatWithUser = userChatStatus.get(selectedUserId);
if (chatWithUser && chatWithUser == currentUser.id) {
    newStatusText = '正在与您聊天';
    newStatusClass = 'chat-status online';
} else {
    const onlineStatus = userOnlineStatus.get(selectedUserId);
    // 优先级：ONLINE > AWAY > 未知
    if (onlineStatus === 'ONLINE') {
        newStatusText = '在线';
        newStatusClass = 'chat-status online';
    } else if (onlineStatus === 'AWAY') {
        newStatusText = '离开';
        newStatusClass = 'chat-status away';
    } else if (!onlineStatus) {
        // 从未收到过该用户的状态消息，默认显示在线（更友好）
        newStatusText = '在线';
        newStatusClass = 'chat-status online';
    } else {
        newStatusText = '离线';
        newStatusClass = 'chat-status offline';
    }
}
```

### 推荐组合方案
**方案2 + 方案3** 同时实施:
1. 修改接收逻辑，避免误删chatWithUserId
2. 修改UI显示逻辑，增加防御性判断

### 验收标准
- [ ] A与B聊天时，B状态显示"正在与您聊天"或"在线"
- [ ] B切换到C聊天后，A看到B的状态仍为"在线"（不是"离线"）
- [ ] B真正关闭浏览器/断开连接后，A看到B的状态变为"离线"
- [ ] B切换回A聊天后，A看到B的状态变为"正在与您聊天"
- [ ] 用户列表中的状态提示文字同步更新

### 涉及文件
- `src/main/resources/static/chat.html`
  - L1254: sendUserStatus调用处
  - L1378-1389: 状态消息接收处理
  - L1393-1453: updateUserStatusUI函数

---

## 🔴 问题5: 消息通知 - 导航栏Badge未更新 + 过滤逻辑失效

### 问题描述
#### 子问题5A: 导航栏通知Badge未显示未读聊天消息数
**现象**:
- 导航栏的通知图标上的红色badge（角标）没有显示未读消息数量
- 或显示了但数值不包含聊天消息

**当前代码** (common.js L461-517):
```javascript
async function updateNotificationBadge(userId) {
    var response = await fetch(API_BASE_URL + '/notifications/unread/count?userId=' + userId);
    var data = await response.json();
    if (data.code === 200) {
        var count = data.data.count;
        var badge = document.getElementById('notificationBadge');
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }
}
```

**可能原因**:
1. **后端API `/notifications/unread/count` 未统计CHAT类型通知**
   - 可能只统计了LIKE、COMMENT、ANSWER等类型
   - 需要检查后端NotificationService的查询SQL
2. **前端定时器未触发或频率过低**
   - 需要检查是否有setInterval定期调用updateNotificationBadge
3. **Badge DOM元素不存在或ID不匹配**
   - 需要检查HTML中是否真的有`id="notificationBadge"`的元素

#### 子问题5B: 通知列表未过滤实时聊天消息
**现象**:
- 消息通知页面/下拉框中显示了**所有消息**，包括：
  - 用户A正在与B聊天时的实时消息（不应该显示）
  - 仅应显示"首次联系"消息（当接收方未主动打开与发送方的聊天窗口时）

**当前过滤逻辑** (common.js L639-653):
```javascript
var currentChatUserId = null;
var chatUserSelect = document.getElementById('chatUserSelect');
if (chatUserSelect && chatUserSelect.value) {
    currentChatUserId = chatUserSelect.value;
}

notifications.forEach(function(notification) {
    if ((notification.type === 'CHAT' || notification.type === 'CHAT_MESSAGE' || notification.type === 'MESSAGE') &&
        notification.fromUserId &&
        currentChatUserId &&
        notification.fromUserId.toString() === currentChatUserId.toString()) {
        return; // 跳过这条消息
    }
    // ... 渲染其他通知
});
```

**问题分析**:
1. **`document.getElementById('chatUserSelect')` 在非聊天页面不存在**
   - 在home.html、questions.html等页面调用renderNotifications时
   - `chatUserSelect` 为null → `currentChatUserId` 为null
   - 过滤条件不满足 → 所有聊天消息都被显示 ❌

2. **过滤条件过于简单**
   - 只检查了`fromUserId === currentChatUserId`
   - 没有考虑"是否已读"、"是否在活跃聊天窗口中查看过"等因素

3. **应该在服务端过滤还是客户端过滤？**
   - 服务端过滤更可靠（可以基于数据库状态）
   - 客户端过滤更灵活（可以实时响应用户行为）
   - **建议**: 两层过滤，服务端返回候选列表，客户端二次过滤

### 修复方案

#### 修复5A: Badge显示问题
**步骤1: 检查后端API**
需要在后端代码中查找`/notifications/unread/count`接口实现，确认是否包含CHAT类型通知。

**步骤2: 前端增强badge更新逻辑**
```javascript
// common.js 修改版
async function updateNotificationBadge(userId) {
    try {
        var response = await fetch(API_BASE_URL + '/notifications/unread/count?userId=' + userId);
        var data = await response.json();

        if (data.code === 200) {
            var count = data.data.count || 0;
            console.log('🔔 未读通知数:', count);

            var badge = document.getElementById('notificationBadge');
            if (badge) {
                if (count > 0) {
                    badge.textContent = count > 99 ? '99+' : count;
                    badge.style.display = 'flex';
                    // 强制设置样式（防止被CSS覆盖）
                    Object.assign(badge.style, {
                        position: 'absolute',
                        top: '-8px',
                        right: '-8px',
                        background: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)',
                        color: 'white',
                        border: '2px solid white',
                        borderRadius: '50%',
                        minWidth: '20px',
                        height: '20px',
                        justifyContent: 'center',
                        alignItems: 'center',
                        fontSize: '12px',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 8px rgba(239, 68, 68, 0.6)',
                        zIndex: '1000'
                    });
                } else {
                    badge.style.display = 'none';
                }
            }
        }
    } catch (error) {
        console.error('更新通知Badge失败:', error);
    }
}
```

**步骤3: 确保定时调用**
```javascript
// 在common.js的初始化函数中
function initNotifications() {
    var user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user.id) {
        updateNotificationBadge(user.id); // 立即执行一次
        // 每30秒更新一次
        setInterval(() => updateNotificationBadge(user.id), 30000);
    }
}

// 在页面加载时调用
window.addEventListener('DOMContentLoaded', initNotifications);
```

#### 修复5B: 过滤逻辑优化
**方案: 基于"活跃聊天窗口"的智能过滤**

```javascript
// common.js 修改版 renderNotifications函数
function renderNotifications(notifications) {
    // 获取当前活跃的聊天对象ID（兼容所有页面）
    var currentActiveChatUserId = getActiveChatUserId();

    var html = '';
    var unreadCount = 0;

    notifications.forEach(function(notification) {
        var isChatMsg = (notification.type === 'CHAT' ||
                        notification.type === 'CHAT_MESSAGE' ||
                        notification.type === 'MESSAGE');

        if (isChatMsg && notification.fromUserId) {
            // 检查是否应该过滤此消息
            if (shouldFilterChatNotification(notification, currentActiveChatUserId)) {
                console.log('🔄 过滤实时聊天消息:', notification.id);
                return; // 跳过
            }
        }

        // 统计未读数（用于更新badge）
        if (!notification.isRead) unreadCount++;

        // 渲染通知项...
        html += '<div class="notification-item ' + (notification.isRead ? 'notification-read' : 'notification-unread') + '">...</div>';
    });

    listContainer.innerHTML = html;
}

/**
 * 判断是否应该过滤某条聊天通知
 * @param {Object} notification - 通知对象
 * @param {string|null} activeChatUserId - 当前活跃聊天对象ID
 * @returns {boolean} true表示应该过滤（不显示）
 */
function shouldFilterChatNotification(notification, activeChatUserId) {
    // 如果当前没有活跃聊天窗口，不过滤（显示所有聊天消息）
    if (!activeChatUserId) return false;

    // 如果通知的发送者不是当前聊天对象，不过滤
    if (notification.fromUserId.toString() !== activeChatUserId.toString()) return false;

    // 如果是当前聊天对象发的消息，检查时间戳
    // 如果消息时间早于打开聊天窗口的时间，说明是历史消息，不应过滤
    var chatOpenTime = localStorage.getItem('chatOpenTime_' + activeChatUserId);
    if (chatOpenTime) {
        var msgTime = new Date(notification.createdAt).getTime();
        var openTime = new Date(chatOpenTime).getTime();
        if (msgTime < openTime) {
            return false; // 打开聊天窗口之前的消息，不过滤
        }
    }

    // 默认过滤当前聊天对象的实时消息
    return true;
}

/**
 * 获取当前活跃的聊天对象ID
 * 兼容chat.html和其他页面
 */
function getActiveChatUserId() {
    // 方法1: 从chat.html的下拉框获取
    var chatUserSelect = document.getElementById('chatUserSelect');
    if (chatUserSelect && chatUserSelect.value) return chatUserSelect.value;

    // 方法2: 从URL参数获取（适用于chat.html?userId=X）
    var urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('userId')) return urlParams.get('userId');

    // 方法3: 从localStorage获取最后聊天的用户ID
    var lastChatUser = localStorage.getItem('lastChatUserId');
    if (lastChatUser) return lastChatUser;

    return null;
}
```

**补充: 记录聊天窗口打开时间**
```javascript
// chat.html - 切换聊天对象时
function selectUser(userId) {
    // 记录打开时间
    localStorage.setItem('chatOpenTime_' + userId, new Date().toISOString());
    localStorage.setItem('lastChatUserId', userId);
    // ... 其他逻辑
}
```

### 验收标准
- [ ] 导航栏通知badge正确显示未读消息总数（包括聊天消息）
- [] badge数值>0时显示红色角标，=0时隐藏
- [] badge数值每30秒自动更新
- [] 通知列表/面板中**不显示**当前正在聊天对象的实时消息
- [] 通知列表**显示**其他用户的未读聊天消息
- [] 通知列表**显示**打开聊天窗口之前的历史消息
- [] 切换聊天对象后，过滤规则即时更新
- [ ] 非聊天页面（home.html等）显示所有未读通知（不过滤）

### 涉及文件
- `src/main/resources/static/js/common.js`
  - L461-517: updateNotificationBadge函数
  - L639-682: renderNotifications函数
  - 需新增: shouldFilterChatNotification函数
  - 需新增: getActiveChatUserId函数
- `src/main/resources/static/chat.html`
  - 切换聊天对象处: 记录chatOpenTime
- **后端文件**（待检查）:
  - NotificationController.java: /notifications/unread/count接口
  - NotificationService.java: 未读计数逻辑

---

## 🔄 全项目回归测试计划

### 测试范围
完成上述5个问题修复后，必须执行以下回归测试：

#### 1. 功能测试矩阵
| 模块 | 测试项 | 预期结果 | 优先级 |
|------|--------|----------|--------|
| 管理后台-用户管理 | 选择每页5条/10条/20条/50条 | 数据行数对应变化 | P0 |
| 管理后台-用户管理 | 点击页码跳转 | 正确跳转并显示对应页数据 | P0 |
| 管理后台-用户管理 | 搜索用户后分页 | 搜索结果正确分页 | P0 |
| 管理后台-问题管理 | 选择每页条数 | 数据行数对应变化 | P0 |
| 前端-问题列表页 | 分页器样式 | 与questions.html一致 | P0 |
| 前端-标签页（如有） | 分页器功能 | 正常工作 | P0 |
| 个人中心-我的问题 | 显示分页器 | 完整显示统计+按钮+选择器 | P0 |
| 个人中心-我的问题 | 选择每页条数 | 数据量立即更新 | P0 |
| 聊天-A/B聊天中B切到C | A看B的状态 | 显示"在线"非"离线" | P0 |
| 聊天-B真正离线 | A看B的状态 | 显示"离线" | P0 |
| 通知-Badge | 有未读消息时 | 显示红色角标+数字 | P0 |
| 通知-列表 | 当前聊天对象消息 | 不显示实时消息 | P0 |
| 通知-列表 | 其他用户消息 | 正常显示 | P0 |

#### 2. 兼容性测试
- [ ] Chrome浏览器最新版
- [ ] Firefox浏览器最新版
- [ ] Edge浏览器最新版
- [ ] 手机端浏览器（Chrome Mobile Safari）

#### 3. 性能测试
- [ ] 管理后台加载1000+用户时分页性能
- [ ] 聊天页面状态更新频率（WebSocket消息处理）
- [ ] 通知Badge更新频率（30秒间隔）

#### 4. 异常场景测试
- [ ] 网络断开时分页操作
- [ ] 并发多用户同时在线聊天
- [ ] 快速连续切换每页条数
- [ ] 数据库无数据时空状态显示

### 回归测试通过标准
- **零P0 BUG**: 所有高优先级功能正常运行
- **零数据丢失**: 修复过程中不破坏已有数据
- **零控制台错误**: 浏览器控制台无JavaScript错误
- **样式一致性**: 所有页面分页器样式统一

---

## 📝 执行顺序建议

**推荐修复顺序**（基于依赖关系和风险）:

1. **问题1** (管理后台分页器) - 最复杂，影响面最广
2. **问题3** (个人中心分页器) - 相对独立，可快速验证模式
3. **问题2** (前端分页器统一样式) - 可复用问题1的模式
4. **问题4** (聊天状态) - 逻辑相对独立
5. **问题5** (消息通知) - 涉及前后端联调
6. **全项目回归测试** - 必须在所有修复完成后执行

---

## ⚠️ 注意事项

1. **备份原文件**: 修改任何文件前先备份
2. **逐步验证**: 每修复一个问题立即测试，不要一次性改完再测
3. **保持向后兼容**: 不要破坏已有的正常功能
4. **注释清晰**: 关键逻辑添加中文注释说明原因
5. **控制台日志**: 保留调试用的console.log，便于排查问题
6. **CSS命名空间**: 避免全局样式污染，使用特定class前缀

---

## ✅ 完成标志

只有满足以下**所有条件**才能认为任务完成：

- [ ] 问题1-5的全部验收标准均已通过
- [ ] 全项目回归测试矩阵中所有测试项均通过
- [ ] 无任何P0/P1级别遗留BUG
- [ ] 用户现场测试确认无误
- [ ] 代码已提交（如用户要求）

---

**文档版本**: v5.0
**创建时间**: 2026-04-10
**预计修复时长**: 2-3小时（含测试）
**风险等级**: 中等（涉及多处核心逻辑修改）
