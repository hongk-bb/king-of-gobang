package com.gobang.backend.controller.user.account;

import com.gobang.backend.service.user.account.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/account")
public class UserAccountController {

    @Autowired
    private UserAccountService userService;

    @PostMapping("/token")
    public Map<String, String> getToken(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        return userService.getToken(username, password);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String confirmPassword = map.get("confirmPassword");
        return userService.register(username, password, confirmPassword);

    }

    @GetMapping("/info")
    public Map<String, String> getInfo(){
        return userService.getInfo();
    }
}
