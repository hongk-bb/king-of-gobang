package com.gobang.backend.service.user.account;

import java.util.Map;


public interface UserAccountService {
    /**
     * 登录：验证用户名密码，验证成功后返回jwt token
     * @param username 用户名
     * @param password 密码
     * @return
     */
    Map<String, String> getToken(String username, String password);

    /**
     * 根据用户的jwt token令牌返回用户信息
     * @return
     */
    Map<String, String> getInfo();

    /**
     * 注册功能
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return
     */
    Map<String, String> register(String username, String password, String confirmPassword);
}
