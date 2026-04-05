package com.example.qa.config;

import com.example.qa.entity.Message;
import com.example.qa.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload Message message) {
        logger.info("========== 收到聊天消息 ==========");
        logger.info("senderId={}, receiverId={}, content={}, questionId={}",
            message.getSenderId(), message.getReceiverId(), message.getContent(), message.getQuestionId());

        try {
            Message savedMessage = messageService.sendMessage(message);
            logger.info("消息已保存到数据库, id={}", savedMessage.getId());

            // 使用 convertAndSendToUser 发送用户特定的消息
            // 这样消息会被发送到 /user/{userId}/queue/messages
            messagingTemplate.convertAndSendToUser(
                String.valueOf(savedMessage.getReceiverId()),
                "/queue/messages",
                savedMessage
            );
            logger.info("消息已发送到接收者: userId={}", savedMessage.getReceiverId());

            messagingTemplate.convertAndSendToUser(
                String.valueOf(savedMessage.getSenderId()),
                "/queue/messages",
                savedMessage
            );
            logger.info("消息已发送到发送者: userId={}", savedMessage.getSenderId());

            // 同时广播消息到 /topic/messages，用于更新会话列表
            messagingTemplate.convertAndSend(
                "/topic/messages",
                savedMessage
            );
            logger.info("消息已广播到 /topic/messages");
            logger.info("========== 消息发送完成 ==========");

        } catch (Exception e) {
            logger.error("发送消息失败: ", e);
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat/read")
    public void markAsRead(@Payload Message message) {
        try {
            messageService.markAsRead(message.getSenderId(), message.getReceiverId());
            
            Message readMessage = new Message();
            readMessage.setSenderId(message.getSenderId());
            readMessage.setReceiverId(message.getReceiverId());
            readMessage.setIsRead(true);
            readMessage.setContent("__READ__");
            
            // 使用 convertAndSendToUser 发送已读通知
            messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getSenderId()),
                "/queue/messages",
                readMessage
            );
            logger.info("已读通知已发送到发送者: userId={}", message.getSenderId());
            
            messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/messages",
                readMessage
            );
            logger.info("已读通知已发送到接收者: userId={}", message.getReceiverId());
            
            // 同时广播通知到 /topic/messages
            messagingTemplate.convertAndSend(
                "/topic/messages",
                readMessage
            );
            logger.info("已读通知已广播到 /topic/messages");
        } catch (Exception e) {
            logger.error("标记消息已读失败: ", e);
        }
    }
}