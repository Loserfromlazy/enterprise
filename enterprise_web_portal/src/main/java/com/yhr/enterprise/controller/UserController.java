package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.entity.Result;
import com.yhr.enterprise.pojo.User;
import com.yhr.enterprise.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @GetMapping("sendSms")
    public Result  sendSms(String phone){
        userService.sendSms(phone);
        return new Result();
    }

    @PostMapping("save")
    public Result save(@RequestBody User user , String smsCode){
        //密码加密
        BCryptPasswordEncoder encoder =new BCryptPasswordEncoder();
        String newpassword =encoder.encode(user.getPassword());
        user.setPassword(newpassword);

        userService.add(user,smsCode);
        return new Result();
    }
}
