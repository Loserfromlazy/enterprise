package com.yhr.enterprise.service;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务
 */
public interface CartService  {
    /**
     * 从redis中提取用户的购物车
     * @param username
     * @return
     */
    public List<Map<String,Object>> findCartList(String username);

    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    public void addItem(String username,String skuId,Integer num);
}
