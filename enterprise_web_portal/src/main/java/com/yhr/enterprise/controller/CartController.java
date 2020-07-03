package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.entity.Result;
import com.yhr.enterprise.service.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cart")
public class CartController {
    @Reference
    private CartService cartService;

    /**
     * 从redis中提取购物车
     * @return
     */
    @GetMapping("/findCartList")
    public List<Map<String,Object>> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> cartList = cartService.findCartList(username);
        return cartList;
    }

    @GetMapping("/addItem")
    public Result addItem(String skuId,Integer num){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username,skuId,num);
        return new Result();
    }
    @GetMapping("/buy")
    public void buy(HttpServletResponse httpServletResponse,String skuId,Integer num) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username,skuId,num);
        httpServletResponse.sendRedirect("/cart.html");
    }
}
