package com.yhr.enterprise.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 获取用户名
     * @return
     */
    @GetMapping("/username")
    public Map username(){
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(user)){
            user="";
        }
        System.out.println("当前登录用户"+user);
        Map map = new HashMap();
        map.put("username",user);
        return map;
    }
}
