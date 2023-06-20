package com.gobang.backend.utils;

import io.jsonwebtoken.Claims;

/**
 * 进行Jwt验证
 */
public class JwtAuthentication {
    /**
     * 静态方法：根据token获取用户的userId
     * @param token
     * @return
     */
    public static Integer getUserId(String token) {
        Integer userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}