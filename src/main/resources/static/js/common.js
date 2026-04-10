const API_BASE_URL = window.location.origin + '/api';
let currentUser = null;
var logoutCallback = null;
var loginConflictModalShown = false;

function updateNav() {
    var user = localStorage.getItem('user');
    var userAvatar = document.getElementById('userAvatar');
    var userInfo = document.getElementById('userInfo');
    var authButtons = document.getElementById('authButtons');
    var loginBtn = document.getElementById('loginBtn');
    var registerBtn = document.getElementById('registerBtn');
    var askBtn = document.getElementById('askBtn');
    var notificationBell = document.getElementById('notificationBell');
    var chatIcon = document.getElementById('chatIcon');
    var adminMenuItems = document.querySelectorAll('.admin-only');
    var tagMenuItems = document.querySelectorAll('.tag-only');
    
    if (user !== null && user !== '') {
        try {
            var userData = JSON.parse(user);
            currentUser = userData;
            
            var userName = userData.username || '用户';
            var avatarUrl = '';
            var defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40"%3E%3Ccircle cx="20" cy="20" r="20" fill="%23667eea"/%3E%3Ctext x="20" y="25" text-anchor="middle" fill="white" font-size="16"%3E' + (userName.charAt(0) || 'U').toUpperCase() + '%3C/text%3E%3C/svg%3E';
            
            if (userData.avatar && typeof userData.avatar === 'string' && userData.avatar !== '' && userData.avatar.trim() !== '') {
                avatarUrl = userData.avatar;
                if (avatarUrl.startsWith('data:image/svg+xml;base64,')) {
                    avatarUrl = avatarUrl.replace('data:image/svg+xml;base64,', 'data:image/svg+xml,');
                }
            } else {
                avatarUrl = defaultAvatar;
            }
            
            if (userInfo) userInfo.classList.remove('hidden');
            if (authButtons) authButtons.classList.add('hidden');
            if (loginBtn) loginBtn.classList.add('hidden');
            if (registerBtn) registerBtn.classList.add('hidden');
            if (notificationBell) notificationBell.classList.remove('hidden');
            if (chatIcon) chatIcon.classList.remove('hidden');
            if (userAvatar) {
                userAvatar.src = avatarUrl;
                userAvatar.onerror = function() { this.src = defaultAvatar; };
            }
            if (askBtn) askBtn.classList.add('hidden');
            
            if (userData.role === 'ADMIN') {
                adminMenuItems.forEach(function(item) { item.classList.remove('hidden'); });
            } else {
                adminMenuItems.forEach(function(item) { item.classList.add('hidden'); });
            }
            tagMenuItems.forEach(function(item) { item.classList.remove('hidden'); });
        } catch (error) {
            localStorage.removeItem('user');
            currentUser = null;
            if (userInfo) userInfo.classList.add('hidden');
            if (authButtons) authButtons.classList.remove('hidden');
            if (loginBtn) loginBtn.classList.remove('hidden');
            if (registerBtn) registerBtn.classList.remove('hidden');
            if (notificationBell) notificationBell.classList.add('hidden');
            if (chatIcon) chatIcon.classList.add('hidden');
            if (askBtn) askBtn.classList.add('hidden');
            adminMenuItems.forEach(function(item) { item.classList.add('hidden'); });
            tagMenuItems.forEach(function(item) { item.classList.add('hidden'); });
        }
    } else {
        currentUser = null;
        if (userInfo) userInfo.classList.add('hidden');
        if (authButtons) authButtons.classList.remove('hidden');
        if (loginBtn) loginBtn.classList.remove('hidden');
        if (registerBtn) registerBtn.classList.remove('hidden');
        if (notificationBell) notificationBell.classList.add('hidden');
        if (chatIcon) chatIcon.classList.add('hidden');
        if (askBtn) askBtn.classList.add('hidden');
        adminMenuItems.forEach(function(item) { item.classList.add('hidden'); });
    }
}

function toggleUserDropdown() {
    var dropdown = document.getElementById('userDropdown');
    dropdown.classList.toggle('show');
}

function hideUserDropdown() {
    var dropdown = document.getElementById('userDropdown');
    dropdown.classList.remove('show');
}

function logout() {
    var user = localStorage.getItem('user');
    var userId = null;
    
    try {
        var userData = JSON.parse(user);
        userId = userData.id;
    } catch (e) {}
    
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    currentUser = null;
    loginConflictModalShown = false;
    
    var userAvatar = document.getElementById('userAvatar');
    var userInfo = document.getElementById('userInfo');
    var authButtons = document.getElementById('authButtons');
    var loginBtn = document.getElementById('loginBtn');
    var registerBtn = document.getElementById('registerBtn');
    var askBtn = document.getElementById('askBtn');
    var adminMenuItems = document.querySelectorAll('.admin-only');
    var notificationBell = document.getElementById('notificationBell');
    var notificationPanel = document.getElementById('notificationPanel');
    var chatIcon = document.getElementById('chatIcon');
    
    if (logoutCallback) {
        logoutCallback();
        logoutCallback = null;
    }
        
    if (userInfo) {
        userInfo.classList.add('hidden');
    }
    if (userAvatar) {
        userAvatar.src = '';
    }
    if (notificationBell) {
        notificationBell.classList.add('hidden');
    }
    if (chatIcon) {
        chatIcon.classList.add('hidden');
    }
    if (notificationPanel) {
        notificationPanel.classList.remove('show');
    }
    
    if (authButtons) {
        authButtons.classList.remove('hidden');
    }
    if (loginBtn) loginBtn.classList.remove('hidden');
    if (registerBtn) registerBtn.classList.remove('hidden');
    if (askBtn) askBtn.classList.add('hidden');
    
    adminMenuItems.forEach(function(item) {
        item.classList.add('hidden');
    });
    
    if (userId) {
        var logoutUrl = API_BASE_URL + '/users/logout?userId=' + userId;
        console.log('Calling logout API:', logoutUrl);
        
        fetch(logoutUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function(response) {
            console.log('Logout API response:', response.status);
            showMessage('退出登录成功', 'success');
            setTimeout(function() {
                window.location.href = '/login.html';
            }, 500);
        }).catch(function(error) {
            console.error('Logout API error:', error);
            showMessage('退出登录成功', 'success');
            setTimeout(function() {
                window.location.href = '/login.html';
            }, 500);
        });
    } else {
        showMessage('退出登录成功', 'success');
        setTimeout(function() {
            window.location.href = '/login.html';
        }, 500);
    }
}

function forceLogoutLocal() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    currentUser = null;
    loginConflictModalShown = false;
    
    var userAvatar = document.getElementById('userAvatar');
    var userInfo = document.getElementById('userInfo');
    var authButtons = document.getElementById('authButtons');
    var loginBtn = document.getElementById('loginBtn');
    var registerBtn = document.getElementById('registerBtn');
    var askBtn = document.getElementById('askBtn');
    var adminMenuItems = document.querySelectorAll('.admin-only');
    var notificationBell = document.getElementById('notificationBell');
    var notificationPanel = document.getElementById('notificationPanel');
    var chatIcon = document.getElementById('chatIcon');
    
    if (logoutCallback) {
        logoutCallback();
        logoutCallback = null;
    }
        
    if (userInfo) {
        userInfo.classList.add('hidden');
    }
    if (userAvatar) {
        userAvatar.src = '';
    }
    if (notificationBell) {
        notificationBell.classList.add('hidden');
    }
    if (chatIcon) {
        chatIcon.classList.add('hidden');
    }
    if (notificationPanel) {
        notificationPanel.classList.remove('show');
    }
    
    if (authButtons) {
        authButtons.classList.remove('hidden');
    }
    if (loginBtn) loginBtn.classList.remove('hidden');
    if (registerBtn) registerBtn.classList.remove('hidden');
    if (askBtn) askBtn.classList.add('hidden');
    
    adminMenuItems.forEach(function(item) {
        item.classList.add('hidden');
    });
    
    window.location.href = '/login.html';
}

function showConfirmModal(title, message, onConfirm, onCancel) {
    var existingModal = document.getElementById('confirmModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    window.confirmModalOnConfirm = onConfirm;
    window.confirmModalOnCancel = onCancel;
    
    var buttonsHtml = '';
    if (onCancel) {
        buttonsHtml = '<button style="background: #4b5563; color: white; border: none; padding: 8px 24px; border-radius: 4px; cursor: pointer;" onclick="closeConfirmModal()">取消</button>' +
                      '<button style="background: #3b82f6; color: white; border: none; padding: 8px 24px; border-radius: 4px; cursor: pointer;" onclick="confirmModalAction()">确定</button>';
    } else {
        buttonsHtml = '<button style="background: #3b82f6; color: white; border: none; padding: 8px 24px; border-radius: 4px; cursor: pointer;" onclick="confirmModalAction()">确定</button>';
    }
    
    var modalHtml = 
        '<div id="confirmModal" style="display: flex; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 10000; justify-content: center; align-items: center;">' +
        '<div style="background: #1e2530; border-radius: 8px; max-width: 400px; width: 90%; box-shadow: 0 4px 20px rgba(0,0,0,0.3);">' +
        '<div style="display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; border-bottom: 1px solid #2d3748;">' +
        '<div style="color: #e0e6ed; font-size: 18px; font-weight: bold;">' + title + '</div>' +
        '<span style="color: #a0aec0; font-size: 24px; cursor: pointer;" onclick="closeConfirmModal()">&times;</span>' +
        '</div>' +
        '<div style="padding: 20px; color: #e0e6ed; line-height: 1.6;">' + message + '</div>' +
        '<div style="display: flex; justify-content: center; gap: 10px; padding: 15px 20px; border-top: 1px solid #2d3748;">' + buttonsHtml + '</div>' +
        '</div>' +
        '</div>';
    
    var div = document.createElement('div');
    div.innerHTML = modalHtml;
    document.body.appendChild(div);
    console.log('Modal created and appended to body');
}

function confirmModalAction() {
    closeConfirmModal();
    if (window.confirmModalOnConfirm) {
        window.confirmModalOnConfirm();
        window.confirmModalOnConfirm = null;
    }
}

function closeConfirmModal() {
    var modal = document.getElementById('confirmModal');
    if (modal) {
        modal.remove();
    }
    if (window.confirmModalOnCancel) {
        var cancelCallback = window.confirmModalOnCancel;
        window.confirmModalOnCancel = null;
        cancelCallback();
    }
}

function showMessage(message, type) {
    type = type || 'success';
    var messageDiv = document.createElement('div');
    messageDiv.className = type === 'success' ? 'success-message' : 'error-message';
    messageDiv.textContent = message;
    
    var container = document.getElementById('messageContainer');
    container.appendChild(messageDiv);
    
    setTimeout(function() {
        messageDiv.remove();
    }, 3000);
}

function escapeHtml(text) {
    if (!text) return '';
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return '';
    var date = new Date(dateString);
    return date.toLocaleString('zh-CN');
}

async function apiRequest(url, options) {
    options = options || {};
    options.headers = options.headers || {};
    
    var token = localStorage.getItem('token');
    if (token) {
        options.headers['Authorization'] = 'Bearer ' + token;
    }
    
    var response = await fetch(API_BASE_URL + url, options);
    var data = await response.json();
    return data;
}

function closeModal() {
    var dropdown = document.getElementById('userDropdown');
    if (dropdown && dropdown.classList.contains('show')) {
        dropdown.classList.remove('show');
    }
}

document.addEventListener('click', function(event) {
    var dropdown = document.getElementById('userDropdown');
    var userAvatar = document.getElementById('userAvatar');
    
    if (dropdown && dropdown.classList.contains('show')) {
        if (!dropdown.contains(event.target) && (!userAvatar || !userAvatar.contains(event.target))) {
            dropdown.classList.remove('show');
        }
    }
});

const THEMES = [
    { name: 'default', label: '赛博朋克', icon: '🌙', description: '深色科技风' },
    { name: 'light', label: '清新简约', icon: '☀️', description: '明亮清爽风' },
    { name: 'dark', label: '深邃暗黑', icon: '🌑', description: '纯黑专业风' },
    { name: 'high-contrast', label: '高对比度', icon: '⚡', description: '高对比度无障碍' }
];

function loadGlobalTheme() {
    loadSystemConfig();
    return THEMES[0];
}

function loadTheme() {
    return loadGlobalTheme();
}

function toggleGlobalTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'default';
    const currentIndex = THEMES.findIndex(theme => theme.name === currentTheme);
    const nextIndex = (currentIndex + 1) % THEMES.length;
    const nextTheme = THEMES[nextIndex];
    
    document.documentElement.setAttribute('data-theme', nextTheme.name);
    localStorage.setItem('theme', nextTheme.name);
    
    updateThemeButtons(nextTheme);
    
    showMessage('已切换到' + nextTheme.label + '主题', 'success');
    return nextTheme;
}

function toggleTheme() {
    return toggleGlobalTheme();
}

function updateThemeButtons(theme) {
    const themeIcon = document.getElementById('themeIcon');
    const themeName = document.getElementById('themeName');
    
    if (themeIcon) themeIcon.textContent = theme.icon;
    if (themeName) themeName.textContent = theme.label;
}

function getCurrentTheme() {
    const themeName = document.documentElement.getAttribute('data-theme') || 'default';
    return THEMES.find(t => t.name === themeName) || THEMES[0];
}

// 立即执行关键初始化操作
updateNav();

// DOM 加载完成后执行其他操作
document.addEventListener('DOMContentLoaded', function() {
    // 立即执行必要的操作
    loadSystemConfig();
    
    // 延迟执行非关键操作，提高页面加载速度
    setTimeout(() => {
        var isChatPage = window.location.pathname.indexOf('chat.html') !== -1;
        
        updateNotificationBadge();
        
        if (isChatPage) {
            // 聊天页面：降低通知轮询频率，避免干扰WebSocket通信
            setInterval(updateNotificationBadge, 60000);
            setInterval(checkLoginStatus, 60000);
        } else {
            setInterval(updateNotificationBadge, 15000);
            setInterval(checkLoginStatus, 30000);
        }
    }, 50);
});

function checkLoginStatus() {
    var user = localStorage.getItem('user');
    if (!user) return;
    
    if (loginConflictModalShown) return;
    
    try {
        var userData = JSON.parse(user);
        console.log('checkLoginStatus - userId:', userData.id, 'loginToken:', userData.loginToken ? 'exists' : 'null');
        
        if (userData.id && userData.loginToken) {
            fetch(API_BASE_URL + '/users/me?userId=' + userData.id + '&loginToken=' + userData.loginToken)
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    console.log('checkLoginStatus response:', data.code, data.message);
                    if (data.code !== 200 && !loginConflictModalShown) {
                        console.log('Login conflict detected, showing modal...');
                        loginConflictModalShown = true;
                        showConfirmModal('账号登录提醒', '您的账号已在其他设备登录，您已被强制下线。', function() {
                            forceLogoutLocal();
                        }, null);
                    }
                })
                .catch(function(error) {
                    console.error('checkLoginStatus error:', error);
                });
        }
    } catch (e) {
        console.error('checkLoginStatus exception:', e);
    }
}

async function updateNotificationBadge() {
    var user = localStorage.getItem('user');
    if (!user) return;
    
    try {
        var userData = JSON.parse(user);
        var userId = userData.id;
        
        let retries = 5;
        let delay = 500;
        let success = false;
        
        while (retries > 0 && !success) {
            try {
                var response = await fetch(API_BASE_URL + '/notifications/unread/count?userId=' + userId, {
                    timeout: 3000
                });
                
                if (!response.ok) {
                    throw new Error('网络响应失败: ' + response.status);
                }
                
                var data = await response.json();
                
                if (data.code === 200) {
                    var count = data.data.count || 0;
                    console.log('🔔 未读通知数:', count);
                    var badge = document.getElementById('notificationBadge');
                    if (badge) {
                        if (count > 0) {
                            badge.textContent = count > 99 ? '99+' : count;
                            badge.style.display = 'flex';
                            // 确保徽章样式正确（强制设置，防止被CSS覆盖）
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
                    success = true;
                } else {
                    throw new Error('获取通知数量失败: ' + (data.message || '未知错误'));
                }
            } catch (error) {
                console.error('获取通知数量失败:', error);
                retries--;
                
                if (retries > 0) {
                    console.log('重试获取通知数量...', retries, '次');
                    await new Promise(resolve => setTimeout(resolve, delay));
                    delay *= 1.5;
                }
            }
        }
    } catch (error) {
        console.error('获取通知数量失败:', error);
    }
}

function toggleNotificationPanel() {
    var panel = document.getElementById('notificationPanel');
    if (panel) {
        panel.classList.toggle('show');
        if (panel.classList.contains('show')) {
            loadNotifications();
        }
    }
}

// 鼠标悬停显示通知面板
function showNotificationPanel() {
    var panel = document.getElementById('notificationPanel');
    if (panel) {
        panel.classList.add('show');
        loadNotifications();
    }
}

function hideNotificationPanel() {
    var panel = document.getElementById('notificationPanel');
    if (panel) {
        panel.classList.remove('show');
    }
}

function toggleNotificationPanelMobile() {
    var panel = document.getElementById('notificationPanel');
    if (panel) {
        if (panel.classList.contains('show')) {
            panel.classList.remove('show');
        } else {
            panel.classList.add('show');
            loadNotifications();
        }
    }
}

async function loadNotifications() {
    var user = localStorage.getItem('user');
    if (!user) return;
    
    try {
        var userData = JSON.parse(user);
        var userId = userData.id;
        
        var listContainer = document.getElementById('notificationList');
        if (!listContainer) return;
        
        listContainer.innerHTML = '<div class="notification-loading">加载中...</div>';
        
        let retries = 5;
        let delay = 500;
        let success = false;
        
        while (retries > 0 && !success) {
            try {
                var response = await fetch(API_BASE_URL + '/notifications/unread?userId=' + userId, {
                    timeout: 5000
                });
                
                if (!response.ok) {
                    throw new Error('网络响应失败: ' + response.status);
                }
                
                var data = await response.json();
                
                if (data.code === 200) {
                    var notifications = data.data;
                    renderNotifications(notifications);
                    success = true;
                } else if (data.code === 401) {
                    listContainer.innerHTML = '<div class="notification-empty">暂无通知</div>';
                    success = true;
                } else {
                    throw new Error('加载失败: ' + (data.message || '未知错误'));
                }
            } catch (error) {
                console.error('加载通知失败:', error);
                retries--;
                
                if (retries > 0) {
                    console.log('重试加载通知...', retries, '次');
                    await new Promise(resolve => setTimeout(resolve, delay));
                    delay *= 1.5;
                } else {
                    // 显示友好的错误信息，并提供重试按钮
                    listContainer.innerHTML = `
                        <div class="notification-empty">
                            <p>加载通知失败</p>
                            <button onclick="loadNotifications()" style="margin-top: 10px; padding: 8px 16px; background: #3b82f6; color: white; border: none; border-radius: 4px; cursor: pointer;">重试</button>
                        </div>
                    `;
                }
            }
        }
    } catch (error) {
        console.error('加载通知失败:', error);
        var listContainer = document.getElementById('notificationList');
        if (listContainer) {
            listContainer.innerHTML = `
                <div class="notification-empty">
                    <p>加载通知失败</p>
                    <button onclick="loadNotifications()" style="margin-top: 10px; padding: 8px 16px; background: #3b82f6; color: white; border: none; border-radius: 4px; cursor: pointer;">重试</button>
                </div>
            `;
        }
    }
}

/**
 * 获取当前活跃的聊天对象ID（兼容所有页面）
 * @returns {string|null} 当前活跃聊天对象的用户ID
 */
function getActiveChatUserId() {
    // 只有在chat.html页面时才认为有活跃聊天
    // 其他页面（home/questions/profile等）不应该过滤聊天消息
    var isChatPage = window.location.pathname.includes('chat') ||
                     document.getElementById('chatUserSelect');

    if (!isChatPage) {
        console.log('📱 非聊天页面，返回null（不过滤任何聊天消息）');
        return null; // 非聊天页面，返回null表示不过滤
    }

    // 方法1: 从chat.html的下拉框获取
    var chatUserSelect = document.getElementById('chatUserSelect');
    if (chatUserSelect && chatUserSelect.value) {
        console.log('📱 当前活跃聊天对象(从select获取):', chatUserSelect.value);
        return chatUserSelect.value;
    }

    // 方法2: 从URL参数获取（适用于chat.html?userId=X）
    var urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('userId')) {
        console.log('📱 当前活跃聊天对象(从URL获取):', urlParams.get('userId'));
        return urlParams.get('userId');
    }

    // 方法3: 从localStorage获取最后聊天的用户ID（仅在chat页面有效）
    var lastChatUser = localStorage.getItem('lastChatUserId');
    if (lastChatUser) {
        console.log('📱 当前活跃聊天对象(从localStorage获取):', lastChatUser);
        return lastChatUser;
    }

    return null;
}

/**
 * 判断是否应该过滤某条聊天通知
 * @param {Object} notification - 通知对象
 * @param {string|null} activeChatUserId - 当前活跃聊天对象ID
 * @returns {boolean} true表示应该过滤（不显示），false表示应该显示
 */
function shouldFilterChatNotification(notification, activeChatUserId) {
    // 如果当前没有活跃聊天窗口，不过滤（显示所有聊天消息）
    if (!activeChatUserId) return false;

    // 如果通知的发送者不是当前聊天对象，不过滤
    if (!notification.fromUserId || notification.fromUserId.toString() !== activeChatUserId.toString()) return false;

    // 如果是当前聊天对象发的消息，检查时间戳
    // 如果消息时间早于打开聊天窗口的时间，说明是历史消息，不应过滤
    var chatOpenTime = localStorage.getItem('chatOpenTime_' + activeChatUserId);
    if (chatOpenTime && notification.createdAt) {
        try {
            var msgTime = new Date(notification.createdAt).getTime();
            var openTime = new Date(chatOpenTime).getTime();
            if (msgTime < openTime) {
                return false; // 打开聊天窗口之前的历史消息，不过滤
            }
        } catch (e) {
            console.warn('解析消息时间失败:', e);
        }
    }

    // 默认过滤当前聊天对象的实时消息
    console.log('🔄 过滤实时聊天消息:', notification.id, 'from:', notification.fromUsername);
    return true;
}

function renderNotifications(notifications) {
    var listContainer = document.getElementById('notificationList');
    if (!listContainer) return;

    var html = '';

    if (!notifications || notifications.length === 0) {
        html = '<div class="notification-empty">暂无通知</div>';
    } else {
        // 获取当前活跃的聊天对象ID（兼容所有页面）
        var currentActiveChatUserId = getActiveChatUserId();
        console.log('📱 当前活跃聊天对象ID:', currentActiveChatUserId);

        notifications.forEach(function(notification) {
            var isChatMsg = (notification.type === 'CHAT' || notification.type === 'CHAT_MESSAGE' || notification.type === 'MESSAGE');

            if (isChatMsg && notification.fromUserId) {
                // 使用智能过滤函数判断是否应该过滤此消息
                if (shouldFilterChatNotification(notification, currentActiveChatUserId)) {
                    return; // 跳过这条消息
                }
            }

            var typeIcon = '🔔';
            if (notification.type === 'LIKE') {
                typeIcon = '👍';
            } else if (notification.type === 'CHAT' || notification.type === 'CHAT_MESSAGE' || notification.type === 'MESSAGE') {
                typeIcon = '💬';
            } else if (notification.type === 'COMMENT') {
                typeIcon = '💬';
            } else if (notification.type === 'ANSWER') {
                typeIcon = '✅';
            }

            var readClass = notification.isRead ? 'notification-read' : 'notification-unread';
            var timeAgo = getTimeAgo(notification.createdAt);

            html += '<div class="notification-item ' + readClass + '" id="notification-' + notification.id + '" ' +
                'data-notification-id="' + notification.id + '" ' +
                'data-notification-type="' + notification.type + '" ' +
                'data-from-user-id="' + (notification.fromUserId || '') + '" ' +
                'data-from-username="' + escapeHtml(notification.fromUsername || '') + '" ' +
                'data-question-id="' + (notification.questionId || '') + '">' +
                '<div class="notification-icon">' + typeIcon + '</div>' +
                '<div class="notification-content">' +
                '<div class="notification-title">' + escapeHtml(notification.content) + '</div>' +
                '<div class="notification-time">' + timeAgo + '</div>' +
                '</div>' +
                '</div>';
        });
    }

    listContainer.innerHTML = html;
    
    // 确保通知面板不会阻止点击事件
    var notificationPanel = document.getElementById('notificationPanel');
    if (notificationPanel) {
        notificationPanel.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    }
    
    var items = listContainer.querySelectorAll('.notification-item');
    items.forEach(function(item) {
        var newItem = item.cloneNode(true);
        item.parentNode.replaceChild(newItem, item);
        
        var isProcessing = false;
        
        newItem.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            if (isProcessing) return;
            isProcessing = true;
            setTimeout(function() { isProcessing = false; }, 800);
            
            var notificationId = this.getAttribute('data-notification-id');
            var notificationType = this.getAttribute('data-notification-type');
            var fromUserId = this.getAttribute('data-from-user-id');
            var fromUsername = this.getAttribute('data-from-username');
            var questionId = this.getAttribute('data-question-id');
            
            console.log('通知点击:', {
                notificationId: notificationId,
                notificationType: notificationType,
                fromUserId: fromUserId,
                fromUsername: fromUsername,
                questionId: questionId
            });
            
            if (notificationType === 'CHAT') {
                handleChatNotificationClick(fromUserId, fromUsername, notificationId);
            } else {
                handleNotificationClick(notificationId, questionId, notificationType);
            }
        }, { passive: false });
        
        newItem.addEventListener('touchend', function(e) {
            e.preventDefault();
            if (isProcessing) return;
            isProcessing = true;
            setTimeout(function() { isProcessing = false; }, 800);
            
            var notificationId = this.getAttribute('data-notification-id');
            var notificationType = this.getAttribute('data-notification-type');
            var fromUserId = this.getAttribute('data-from-user-id');
            var fromUsername = this.getAttribute('data-from-username');
            var questionId = this.getAttribute('data-question-id');
            
            if (notificationType === 'CHAT') {
                handleChatNotificationClick(fromUserId, fromUsername, notificationId);
            } else {
                handleNotificationClick(notificationId, questionId, notificationType);
            }
        }, { passive: false });
    });
}

function handleChatNotificationClick(fromUserId, fromUsername, notificationId) {
    console.log('处理聊天通知点击:', fromUserId, fromUsername, notificationId);
    
    var item = document.getElementById('notification-' + notificationId);
    if (item) {
        item.remove();
    }
    
    markNotificationAsRead(notificationId).then(function() {
        updateNotificationBadge();
    });
    
    window.location.href = '/chat.html?userId=' + fromUserId;
}

async function handleNotificationClick(notificationId, questionId, type) {
    console.log('处理通知点击:', notificationId, questionId, type);
    
    var item = document.getElementById('notification-' + notificationId);
    if (item) {
        item.remove();
    }
    
    try {
        await markNotificationAsRead(notificationId);
    } catch(e) {
        console.error('标记已读失败:', e);
    }
    
    try {
        await updateNotificationBadge();
    } catch(e) {
        console.error('更新徽章失败:', e);
    }
    
    if (questionId) {
        window.location.href = '/question-detail.html?id=' + questionId;
    }
}

async function markNotificationAsRead(notificationId) {
    let retries = 3;
    let delay = 1000;
    
    while (retries > 0) {
        try {
            var user = localStorage.getItem('user');
            var userId = user ? JSON.parse(user).id : null;
            var url = API_BASE_URL + '/notifications/' + notificationId + '/read';
            if (userId) url += '?userId=' + userId;
            
            var response = await fetch(url, { method: 'PUT' });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            return true;
        } catch (error) {
            console.error('标记通知已读失败:', error);
            retries--;
            
            if (retries > 0) {
                console.log('重试标记通知已读...', retries, '次');
                await new Promise(resolve => setTimeout(resolve, delay));
                delay *= 2;
            }
        }
    }
    return false;
}



async function markAllNotificationsRead() {
    var user = localStorage.getItem('user');
    if (!user) return;
    
    try {
        var userData = JSON.parse(user);
        var userId = userData.id;
        
        let retries = 3;
        let delay = 1000;
        let success = false;
        
        while (retries > 0 && !success) {
            try {
                var response = await fetch(API_BASE_URL + '/notifications/read/all?userId=' + userId, {
                    method: 'PUT'
                });
                
                if (!response.ok) {
                    throw new Error('网络响应失败: ' + response.status);
                }
                
                var data = await response.json();
                
                if (data.code === 200) {
                    var listContainer = document.getElementById('notificationList');
                    if (listContainer) {
                        listContainer.innerHTML = '<div class="notification-empty">暂无通知</div>';
                    }
                    
                    try {
                        await updateNotificationBadge();
                    } catch(e) {
                        console.error('更新徽章失败:', e);
                    }
                    
                    success = true;
                } else {
                    throw new Error('全部标记已读失败: ' + (data.message || '未知错误'));
                }
            } catch (error) {
                console.error('全部标记已读失败:', error);
                retries--;
                
                if (retries > 0) {
                    console.log('重试全部标记已读...', retries, '次');
                    await new Promise(resolve => setTimeout(resolve, delay));
                    delay *= 2;
                }
            }
        }
    } catch (error) {
        console.error('全部标记已读失败:', error);
    }
}

async function deleteNotification(notificationId) {
    try {
        await fetch(API_BASE_URL + '/notifications/' + notificationId, {
            method: 'DELETE'
        });
    } catch (error) {
        console.error('删除通知失败:', error);
    }
}

async function deleteAllNotifications() {
    var user = localStorage.getItem('user');
    if (!user) return;
    
    try {
        var userData = JSON.parse(user);
        var userId = userData.id;
        await fetch(API_BASE_URL + '/notifications/all?userId=' + userId, {
            method: 'DELETE'
        });
    } catch (error) {
        console.error('删除全部通知失败:', error);
    }
}

function getTimeAgo(dateString) {
    if (!dateString) return '';
    var date = new Date(dateString);
    var now = new Date();
    var diff = now - date;
    
    var minutes = Math.floor(diff / 60000);
    var hours = Math.floor(diff / 3600000);
    var days = Math.floor(diff / 86400000);
    
    if (minutes < 1) return '刚刚';
    if (minutes < 60) return minutes + '分钟前';
    if (hours < 24) return hours + '小时前';
    if (days < 30) return days + '天前';
    
    return date.toLocaleDateString('zh-CN');
}

// 全局系统配置对象
var globalSystemConfig = null;

// 立即应用默认配置，避免显示白色背景
(function() {
    var defaultConfig = {
        siteName: 'IT技术问答社区',
        siteDescription: '探索技术前沿，分享编程智慧，共同成长进步',
        backgroundType: 'gradient',
        backgroundValue: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        layoutType: 'default',
        primaryColor: '#ec4899',
        secondaryColor: '#8b5cf6'
    };
    globalSystemConfig = defaultConfig;
    applySystemConfig(defaultConfig);
})();

// 从localStorage加载配置
function loadConfigFromStorage() {
    try {
        var cachedConfig = localStorage.getItem('systemConfig');
        var cacheTime = localStorage.getItem('systemConfigTime');
        if (cachedConfig && cacheTime) {
            var config = JSON.parse(cachedConfig);
            globalSystemConfig = config;
            applySystemConfig(config);
            return true;
        }
    } catch (e) {
        console.error('从本地存储加载配置失败:', e);
    }
    return false;
}

// 从服务器加载配置
async function loadConfigFromServer() {
    let retries = 3; // 减少重试次数
    let delay = 300; // 减少延迟
    const now = Date.now();

    while (retries > 0) {
        try {
            var response = await fetch(API_BASE_URL + '/system-config?t=' + now, {
                timeout: 2000, // 减少超时时间
                cache: 'no-cache'
            });
            
            if (!response.ok) {
                throw new Error('网络响应失败: ' + response.status);
            }
            
            var data = await response.json();

            if (data.code === 200 && data.data) {
                var config = data.data;
                globalSystemConfig = config;
                localStorage.setItem('systemConfig', JSON.stringify(config));
                localStorage.setItem('systemConfigTime', now.toString());
                // 使用 requestAnimationFrame 优化 DOM 操作
                requestAnimationFrame(() => {
                    applySystemConfig(config);
                });
                return true;
            } else {
                throw new Error('配置加载失败: ' + (data.message || '未知错误'));
            }
        } catch (error) {
            console.error('从服务器加载系统配置失败:', error);
            retries--;
            
            if (retries > 0) {
                console.log('重试加载系统配置...', retries, '次');
                await new Promise(resolve => setTimeout(resolve, delay));
                delay *= 1.5; // 指数退避
            }
        }
    }
    return false;
}

async function loadSystemConfig() {
    console.log('🔄 开始加载系统配置...');
    // 首先尝试从本地存储加载（同步）
    const loadedFromStorage = loadConfigFromStorage();
    console.log('📱 从本地存储加载配置:', loadedFromStorage ? '成功' : '失败');
    
    // 然后异步从服务器更新，不阻塞页面渲染
    setTimeout(async () => {
        console.log('🌐 从服务器加载最新配置...');
        const loadedFromServer = await loadConfigFromServer();
        console.log('🌐 从服务器加载配置:', loadedFromServer ? '成功' : '失败');
    }, 100); // 延迟一点时间，确保页面先渲染
}

// 立即加载配置
loadSystemConfig();

function applySystemConfig(config) {
    if (config.siteName) {
        var siteNameElements = document.querySelectorAll('.site-name');
        siteNameElements.forEach(function(el) {
            el.textContent = config.siteName;
        });
        var logoTextElements = document.querySelectorAll('.logo');
        logoTextElements.forEach(function(el) {
            if (!el.querySelector('.logo-img')) {
                el.textContent = config.siteName;
            }
        });
    }
    
    if (config.backgroundType && config.backgroundValue) {
        document.body.style.backgroundSize = 'cover';
        document.body.style.backgroundPosition = 'center';
        document.body.style.backgroundAttachment = 'fixed';
        
        var bgType = config.backgroundType.toUpperCase();
        if (bgType === 'COLOR') {
            document.body.style.backgroundColor = config.backgroundValue;
            document.body.style.backgroundImage = 'none';
            adjustTextColorsForBackground(config.backgroundValue);
        } else if (bgType === 'IMAGE') {
            document.body.style.backgroundImage = 'url(' + config.backgroundValue + ')';
            setDarkTextOverlay();
        } else if (bgType === 'GRADIENT') {
            document.body.style.backgroundImage = config.backgroundValue;
            setDarkTextOverlay();
        }
    }
    
    if (config.primaryColor) {
        document.documentElement.style.setProperty('--primary-color', config.primaryColor);
        document.documentElement.style.setProperty('--accent-color', config.primaryColor);
        document.documentElement.style.setProperty('--accent-secondary', config.primaryColor);
        
        var primaryStyle = document.getElementById('dynamic-primary-color');
        if (!primaryStyle) {
            primaryStyle = document.createElement('style');
            primaryStyle.id = 'dynamic-primary-color';
            document.head.appendChild(primaryStyle);
        }
        primaryStyle.textContent = `
            .btn-primary, .btn-accent {
                background: linear-gradient(135deg, ${config.primaryColor} 0%, ${config.secondaryColor || config.primaryColor} 100%) !important;
                border-color: ${config.primaryColor} !important;
            }
            .btn-primary:hover, .btn-accent:hover {
                box-shadow: 0 4px 20px ${config.primaryColor}40 !important;
            }
            .nav-link:hover, .nav-link.active {
                color: ${config.primaryColor} !important;
            }
            .nav-link::after {
                background: ${config.primaryColor} !important;
            }
            a:hover, .link-hover:hover {
                color: ${config.primaryColor} !important;
            }
            .tag:hover, .tag-primary {
                background: ${config.primaryColor}20 !important;
                color: ${config.primaryColor} !important;
                border-color: ${config.primaryColor}40 !important;
            }
            ::selection {
                background: ${config.primaryColor}40 !important;
            }
            ::-webkit-scrollbar-thumb {
                background: ${config.primaryColor}60 !important;
            }
            ::-webkit-scrollbar-thumb:hover {
                background: ${config.primaryColor} !important;
            }
            .logo {
                background: linear-gradient(135deg, ${config.primaryColor} 0%, ${config.secondaryColor || config.primaryColor} 50%, ${config.primaryColor} 100%) !important;
                -webkit-background-clip: text !important;
                -webkit-text-fill-color: transparent !important;
                background-clip: text !important;
            }
            .stat-card:hover, .card:hover {
                border-color: ${config.primaryColor}30 !important;
            }
            input:focus, textarea:focus, select:focus {
                border-color: ${config.primaryColor} !important;
                box-shadow: 0 0 0 3px ${config.primaryColor}20 !important;
            }
            .checkbox-custom:checked {
                background: ${config.primaryColor} !important;
                border-color: ${config.primaryColor} !important;
            }
            header, .header {
                background: ${config.primaryColor} !important;
                background: linear-gradient(135deg, ${config.primaryColor} 0%, ${config.secondaryColor || config.primaryColor} 100%) !important;
            }
            header .nav a:hover, header .nav a.active,
            .header .nav a:hover, .header .nav a.active {
                color: #ffffff !important;
            }
            header .nav a span,
            .header .nav a span {
                color: rgba(255, 255, 255, 0.85) !important;
            }
            header .logo, .header .logo {
                color: #ffffff !important;
                background: none !important;
                -webkit-text-fill-color: #ffffff !important;
            }
        `;
    }
    
    if (config.secondaryColor) {
        document.documentElement.style.setProperty('--secondary-color', config.secondaryColor);
        document.documentElement.style.setProperty('--accent-secondary', config.secondaryColor);
    }
    
    if (config.logoUrl) {
        var logoElements = document.querySelectorAll('.logo-img');
        logoElements.forEach(function(el) {
            el.src = config.logoUrl;
        });
    }
    
    if (config.faviconUrl) {
        var link = document.querySelector("link[rel*='icon']") || document.createElement('link');
        link.type = 'image/x-icon';
        link.rel = 'shortcut icon';
        link.href = config.faviconUrl;
        document.getElementsByTagName('head')[0].appendChild(link);
    }
}

function adjustTextColorsForBackground(colorValue) {
    var brightness = getColorBrightness(colorValue);
    var isLightBackground = brightness > 128;
    
    var styleId = 'dynamic-text-colors';
    var existingStyle = document.getElementById(styleId);
    if (existingStyle) {
        existingStyle.remove();
    }
    
    var style = document.createElement('style');
    style.id = styleId;
    
    if (isLightBackground) {
        style.textContent = `
            .card, .question-item, .stat-card {
                background: rgba(255, 255, 255, 0.92) !important;
            }
            .question-item .question-title {
                color: #1f2937 !important;
            }
            .question-item:hover .question-title {
                color: #7c3aed !important;
            }
            .question-item .question-content {
                color: #4b5563 !important;
            }
            .question-item .question-meta {
                color: #6b7280 !important;
            }
            .form-container, .modal-content, .dropdown-menu, .notification-panel {
                background: rgba(255, 255, 255, 0.95) !important;
            }
            .form-container *, .modal-content *, .dropdown-menu * {
                color: #1f2937 !important;
            }
            .notification-panel * {
                color: #1f2937 !important;
            }
            .notification-item {
                color: #1f2937 !important;
            }
            .notification-title {
                color: #1f2937 !important;
            }
            .notification-time {
                color: #6b7280 !important;
            }
            .notification-empty {
                color: #6b7280 !important;
            }
            .tag, .tag-item {
                background: rgba(139, 92, 246, 0.15) !important;
                color: #7c3aed !important;
            }
            input, textarea, select {
                background: white !important;
                color: #1f2937 !important;
                border-color: rgba(107, 114, 128, 0.3) !important;
            }
            input::placeholder, textarea::placeholder {
                color: #9ca3af !important;
            }
            .empty-state, .loading-state {
                color: #6b7280 !important;
            }
            .sidebar-section h3, .sidebar-section .section-title {
                color: #1f2937 !important;
            }
            .sidebar-section .question-list li a {
                color: #4b5563 !important;
            }
            .sidebar-section .question-list li a:hover {
                color: #7c3aed !important;
            }
        `;
    } else {
        style.textContent = `
            .card, .question-item, .stat-card {
                background: rgba(26, 26, 46, 0.6) !important;
            }
            .question-item .question-title {
                color: #e8eaed !important;
            }
            .question-item:hover .question-title {
                color: #ec4899 !important;
            }
            .question-item .question-content {
                color: #94a3b8 !important;
            }
            .question-item .question-meta {
                color: #64748b !important;
            }
            .form-container, .modal-content, .dropdown-menu, .notification-panel {
                background: rgba(26, 26, 46, 0.95) !important;
            }
            .form-container *, .modal-content *, .dropdown-menu * {
                color: #e8eaed !important;
            }
            .notification-panel * {
                color: #e8eaed !important;
            }
            .notification-item {
                color: #e8eaed !important;
            }
            .notification-title {
                color: #e8eaed !important;
            }
            .notification-time {
                color: #94a3b8 !important;
            }
            .notification-empty {
                color: #94a3b8 !important;
            }
            .tag, .tag-item {
                background: rgba(236, 72, 153, 0.15) !important;
                color: #ec4899 !important;
            }
            input, textarea, select {
                background: rgba(30, 30, 50, 0.8) !important;
                color: #e8eaed !important;
                border-color: rgba(236, 72, 153, 0.3) !important;
            }
            input::placeholder, textarea::placeholder {
                color: #6b7280 !important;
            }
            .empty-state, .loading-state {
                color: #94a3b8 !important;
            }
            .sidebar-section h3, .sidebar-section .section-title {
                color: #e8eaed !important;
            }
            .sidebar-section .question-list li a {
                color: #94a3b8 !important;
            }
            .sidebar-section .question-list li a:hover {
                color: #ec4899 !important;
            }
        `;
    }
    
    document.head.appendChild(style);
}

function setDarkTextOverlay() {
    var styleId = 'dynamic-text-colors';
    var existingStyle = document.getElementById(styleId);
    if (existingStyle) {
        existingStyle.remove();
    }
    
    var style = document.createElement('style');
    style.id = styleId;
    style.textContent = `
        .card, .question-item, .stat-card {
            background: rgba(255, 255, 255, 0.92) !important;
        }
        .question-item .question-title {
            color: #1f2937 !important;
        }
        .question-item:hover .question-title {
            color: #7c3aed !important;
        }
        .question-item .question-content {
            color: #4b5563 !important;
        }
        .question-item .question-meta {
            color: #6b7280 !important;
        }
        .form-container, .modal-content, .dropdown-menu, .notification-panel {
            background: rgba(255, 255, 255, 0.95) !important;
        }
        .form-container *, .modal-content *, .dropdown-menu * {
            color: #1f2937 !important;
        }
        .notification-panel * {
            color: #1f2937 !important;
        }
        .notification-item {
            color: #1f2937 !important;
        }
        .notification-title {
            color: #1f2937 !important;
        }
        .notification-time {
            color: #6b7280 !important;
        }
        .notification-empty {
            color: #6b7280 !important;
        }
        .tag, .tag-item {
            background: rgba(139, 92, 246, 0.15) !important;
            color: #7c3aed !important;
        }
        input, textarea, select {
            background: white !important;
            color: #1f2937 !important;
            border-color: rgba(107, 114, 128, 0.3) !important;
        }
        input::placeholder, textarea::placeholder {
            color: #9ca3af !important;
        }
        .empty-state, .loading-state {
            color: #6b7280 !important;
        }
        .sidebar-section h3, .sidebar-section .section-title {
            color: #1f2937 !important;
        }
        .sidebar-section .question-list li a {
            color: #4b5563 !important;
        }
        .sidebar-section .question-list li a:hover {
            color: #7c3aed !important;
        }
    `;
    
    document.head.appendChild(style);
}

function getColorBrightness(colorValue) {
    var hex = colorValue;
    
    if (colorValue.startsWith('rgb')) {
        var match = colorValue.match(/\d+/g);
        if (match && match.length >= 3) {
            var r = parseInt(match[0]);
            var g = parseInt(match[1]);
            var b = parseInt(match[2]);
            return (r * 299 + g * 587 + b * 114) / 1000;
        }
    }
    
    if (colorValue.startsWith('#')) {
        hex = colorValue.slice(1);
        if (hex.length === 3) {
            hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
        }
    }
    
    if (/^[0-9A-Fa-f]{6}$/.test(hex)) {
        var r = parseInt(hex.substr(0, 2), 16);
        var g = parseInt(hex.substr(2, 2), 16);
        var b = parseInt(hex.substr(4, 2), 16);
        return (r * 299 + g * 587 + b * 114) / 1000;
    }
    
    return 128;
}
