# 技术问题修复规范文档 v2.0

## 项目概述
**项目名称：** QA技术问答平台
**项目路径：** `e:\Test\XiangMu\LunTan_Demo\qa-platform`
**修复日期：** 2026-04-09
**优先级：** 高
**版本：** v2.0（第二次修复）

---

## 问题1：管理后台分页器无法点击且默认显示问题

### 问题描述
1. 用户管理和问题管理的分页器有页码显示但无法点击选择
2. 问题管理的页码数据没有默认显示10条

### 影响范围
- **页面：** 管理后台 `/admin.html`
- **功能：** 用户列表分页、问题列表分页
- **用户影响：** 无法通过分页浏览数据

### 根因分析

#### 问题1：onclick事件绑定方式错误

**文件：** `admin.html` 第2553-2580行

**问题代码：**
```javascript
html += '<button class="page-btn" onclick="(' + callback.name + ')(' + (currentPage - 1) + ')">上一页</button>';
```

**问题分析：**
- `callback` 是箭头函数或匿名函数
- 箭头函数没有 `name` 属性，所以 `callback.name` 是 `undefined`
- 生成的HTML是：`onclick="(undefined)(2)"`
- innerHTML插入后点击事件无法触发

### 修复方案

**方案：使用事件委托处理**

修改 `renderPagination` 函数，使用data属性存储页码，用事件委托处理点击：

```javascript
function renderPagination(containerId, total, currentPage, callback) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const totalPages = Math.ceil(total / pageSize);

    let html = '';
    html += '<div style="color: var(--text-secondary); font-size: 14px; padding: 8px 12px; margin-right: 16px; display: inline-block;">';
    html += '共 ' + total + ' 条，第 ' + currentPage + '/' + totalPages + ' 页</div>';

    if (totalPages > 1) {
        html += '<button class="page-btn" ' + (currentPage === 1 ? 'disabled' : '') + ' data-page="' + (currentPage - 1) + '">上一页</button>';

        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
                html += '<button class="page-btn ' + (i === currentPage ? 'active' : '') + '" data-page="' + i + '">' + i + '</button>';
            }
        }

        html += '<button class="page-btn" ' + (currentPage === totalPages ? 'disabled' : '') + ' data-page="' + (currentPage + 1) + '">下一页</button>';
    }

    container.innerHTML = html;

    // 事件委托处理点击
    container.onclick = function(e) {
        const btn = e.target.closest('.page-btn');
        if (!btn || btn.disabled) return;
        const page = parseInt(btn.dataset.page);
        callback(page);
    };
}
```

---

## 问题2：网站整体响应速度优化

### 问题描述
网站整体响应速度慢，存在卡顿，影响用户体验。

### 可能原因
1. **频繁的API请求** - 通知轮询太频繁
2. **未优化的DOM操作** - 大量DOM操作没有批处理
3. **内存泄漏** - 事件监听器没有清理
4. **重复渲染** - 不必要的重新渲染

### 性能优化方案

#### 优化1：减少通知轮询频率

**文件：** `common.js`

```javascript
// 优化轮询间隔
const isChatPage = window.location.pathname.indexOf('chat.html') !== -1;

if (isChatPage) {
    setInterval(updateNotificationBadge, 60000);  // 1分钟
} else {
    setInterval(updateNotificationBadge, 15000);  // 15秒
}
```

#### 优化2：防抖处理搜索输入

```javascript
function debounce(func, wait) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}
```

---

## 问题3：聊天未读消息不显示

### 问题描述
导航栏的消息通知按钮无法显示其他用户发送的聊天内容未读消息。

### 影响范围
- **页面：** 所有页面（导航栏通用）
- **功能：** 聊天未读消息计数和显示
- **用户影响：** 无法看到新的聊天消息提醒

### 根因分析

**文件：** `common.js` 第638-640行

**问题代码：**
```javascript
var filtered = notifications.filter(function(n) {
    return n.type !== 'CHAT' && n.type !== 'CHAT_MESSAGE' && n.type !== 'MESSAGE';
});
```

**问题：** 这段代码明确过滤掉了所有聊天类型的通知！

### 修复方案

**文件：** `common.js` 第629-666行

修改 `renderNotifications` 函数，显示所有通知类型：

```javascript
function renderNotifications(notifications) {
    var listContainer = document.getElementById('notificationList');
    if (!listContainer) return;

    var html = '';

    if (!notifications || notifications.length === 0) {
        html = '<div class="notification-empty">暂无通知</div>';
    } else {
        notifications.forEach(function(notification) {
            var typeIcon = '🔔';
            if (notification.type === 'LIKE') {
                typeIcon = '👍';
            } else if (notification.type === 'CHAT' || notification.type === 'CHAT_MESSAGE') {
                typeIcon = '💬';
            }

            var readClass = notification.isRead ? 'notification-read' : 'notification-unread';
            var timeAgo = getTimeAgo(notification.createdAt);

            html += '<div class="notification-item ' + readClass + '" id="notification-' + notification.id + '">' +
                '<div class="notification-icon">' + typeIcon + '</div>' +
                '<div class="notification-content">' +
                '<div class="notification-title">' + escapeHtml(notification.content) + '</div>' +
                '<div class="notification-time">' + timeAgo + '</div>' +
                '</div></div>';
        });
    }

    listContainer.innerHTML = html;
}
```

---

## 验收标准

### 问题1验收
- [ ] 分页按钮可点击并正确跳转
- [ ] 单页数据时显示统计信息
- [ ] 多页数据时显示完整分页控件

### 问题2验收
- [ ] 页面加载速度提升
- [ ] 无明显卡顿
- [ ] 聊天页面WebSocket通信不受干扰

### 问题3验收
- [ ] 聊天未读消息显示在通知列表
- [ ] 点击通知可跳转到聊天页面
- [ ] 未读计数正确更新
