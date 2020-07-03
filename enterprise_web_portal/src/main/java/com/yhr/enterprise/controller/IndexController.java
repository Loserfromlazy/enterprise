package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.pojo.Ad;
import com.yhr.enterprise.service.AdService;
import com.yhr.enterprise.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Reference
    private AdService adService;
    @Reference
    private CategoryService categoryService;

    @GetMapping("index")
    public String index(Model model){
        //首页轮播图
        List<Ad> lbList = adService.findByPosition("index_lb");
        model.addAttribute("lbt",lbList);
        //商品分类导航
        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryList",categoryTree);
        return "index";
    }


}
