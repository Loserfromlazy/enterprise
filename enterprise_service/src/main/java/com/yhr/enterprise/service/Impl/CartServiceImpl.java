package com.yhr.enterprise.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yhr.enterprise.pojo.Category;
import com.yhr.enterprise.pojo.OrderItem;
import com.yhr.enterprise.pojo.Sku;
import com.yhr.enterprise.service.CartService;
import com.yhr.enterprise.service.CategoryService;
import com.yhr.enterprise.service.SkuService;
import com.yhr.enterprise.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuService skuService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println("从redis中提取购物车"+username);
        List<Map<String ,Object>> cartList = (List<Map<String ,Object>>) redisTemplate.boundHashOps(CacheKey.CART_LIST).get(username);
        if (cartList==null){
            cartList =new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void addItem(String username, String skuId, Integer num) {
        //遍历购物车，如果购物车中存在商品则累加数量，如果不存在则添加购物车项
        //获取购物车
        List<Map<String, Object>> cartList = findCartList(username);
        boolean flag =false;//是否在购物车存在
        for (Map map:cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (orderItem.getSkuId().equals(skuId)){//购物车中存在商品
                if (orderItem.getNum()<=0){
                    cartList.remove(map);
                    break;
                }
                int weight=orderItem.getWeight()/orderItem.getNum();//单个商品重量

                orderItem.setNum(orderItem.getNum()+num);//数量变更
                orderItem.setPrice(orderItem.getPrice()*orderItem.getNum());//金额变更
                orderItem.setWeight(weight*orderItem.getNum());//重量的变更

                if (orderItem.getNum()<=0){
                    cartList.remove(map);
                }
                flag=true;
                break;
            }
        }
        //如果购物车没有该商品
        if (flag==false){

            Sku sku = skuService.findById(skuId);
            if (sku == null){
                throw new RuntimeException("商品不存在");
            }
            if (!"1".equals(sku.getStatus())){
                throw new RuntimeException("商品状态不合法");
            }
            if (num <= 0 ){//数量不能为0或负数
                throw new RuntimeException("商品数量不合法");
            }

            OrderItem orderItem = new OrderItem();



            orderItem.setSkuId(skuId);
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setNum(num);
            orderItem.setImage(sku.getImage());
            orderItem.setPrice(sku.getPrice());
            orderItem.setName(sku.getName());
            orderItem.setMoney(orderItem.getPrice()*num);
            if (sku.getWeight()==null){
                sku.setWeight(0);
            }
            orderItem.setWeight(sku.getWeight()*num);
            //商品分类
            orderItem.setCategoryId3(sku.getCategoryId());
            Category category3 =(Category)redisTemplate.boundHashOps(CacheKey.CATEGORY).get(sku.getCategoryId());
            if (category3==null){
                category3 = categoryService.findById(sku.getCategoryId());//根据3级分类id查询2级分类
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(sku.getCategoryId(),category3);
            }
            orderItem.setCategoryId2(category3.getParentId());
            Category category2 =(Category)redisTemplate.boundHashOps(CacheKey.CATEGORY).get(category3.getParentId());
            if (category2==null){
                category2 = categoryService.findById(category3.getParentId());//根据2级分类id查询1级分类
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category3.getParentId(),category2);
            }
            orderItem.setCategoryId1(category2.getParentId());

            Map map = new HashMap();
            map.put("item",orderItem);
            map.put("checked",true);

            cartList.add(map);

        }
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username,cartList);
    }
}
