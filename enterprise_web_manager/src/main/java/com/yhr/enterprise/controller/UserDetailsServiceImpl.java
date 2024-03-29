package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.pojo.Admin;
import com.yhr.enterprise.service.AdminService;
import com.yhr.enterprise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //实际项目中应该从数据库中提取角色列表
        Map map= new HashMap<>();
        map.put("loginName",s);
        map.put("status","1");
        List<Admin> list = adminService.findList(map);
        if (list.size() == 0){
            return null;
        }
        List<GrantedAuthority> grantedAuthorities =new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new User(s,list.get(0).getPassword(),grantedAuthorities);
    }
}
