package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.service.SkuSearchService;
import com.yhr.enterprise.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@Controller
public class SearchController {

    @Reference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public String search(Model model, @RequestParam Map<String,String> searchMap) throws Exception {
        //get请求字符集处理
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);
        Map result = skuSearchService.search(searchMap);
        model.addAttribute("result",result);
        return "search";
    }
}
