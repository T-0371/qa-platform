package com.example.qa.util;

import org.springframework.util.DigestUtils;

/**
 * 密码工具类
 * 用于密码加密和验证
 */
public class PasswordUtil {
    
    /**
     * 密码加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }
    
    /**
     * 验证密码
     * @param password 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String password, String encryptedPassword) {
        String encrypted = encrypt(password);
        return encrypted.equals(encryptedPassword);
    }
}