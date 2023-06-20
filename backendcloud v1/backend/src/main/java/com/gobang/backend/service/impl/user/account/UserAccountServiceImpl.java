package com.gobang.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gobang.backend.mapper.UserMapper;
import com.gobang.backend.pojo.User;
import com.gobang.backend.service.impl.utils.UserDetailsImpl;
import com.gobang.backend.service.user.account.UserAccountService;
import com.gobang.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserAccountServiceImpl implements UserAccountService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> getToken(String username, String password) {
        // 将用户名密码进行封装，将密码加密
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken); // 登录失败，会自动处理

        // 登录成功，取出用户信息
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();

        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> map = new HashMap<>();
        map.put("error_msg", "success");
        map.put("token", jwt);
        return map;
    }

    @Override
    public Map<String, String> getInfo() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();

        Map<String, String> map = new HashMap<>();
        map.put("error_msg", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("photo", user.getPhoto());
        return map;
    }

    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null) {
            map.put("error_msg", "用户名不能为空");
            return map;
        }
        if(password == null || confirmPassword == null) {
            map.put("error_msg", "密码不能为空");
            return map;
        }
        username = username.trim();
        if(username.length() == 0) {
            map.put("error_msg", "用户名不能为空");
            return map;
        }
        if(password.length() == 0 || confirmPassword.length() == 0) {
            map.put("error_msg", "密码不能为空");
            return map;
        }
        if(username.length() > 100) {
            map.put("error_msg", "用户名长度不能大于100");
            return map;
        }
        if(password.length() > 100 || confirmPassword.length() > 100) {
            map.put("error_msg", "密码长度不能大于100");
            return map;
        }
        if(!password.equals(confirmPassword)){
            map.put("error_msg", "两次输入的密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()) {
            map.put("error_msg", "用户名已存在");
            return map;
        }
        String encodedPassword = passwordEncoder.encode(password);  // 密码加密
        String photo = "https://www.qzqn8.com/wp-content/uploads/2022/12/7-5.jpg";
        User user = new User(null, username, encodedPassword, photo,1500);

        userMapper.insert(user);
        map.put("error_msg", "success");
        return map;
    }
}
